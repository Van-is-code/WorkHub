import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class ApiService {
  static const String baseUrl = 'http://localhost:8080/workhub/api/v1';

  // Đăng nhập giống FE: POST /login, body null, email & password là query param
  static Future<String?> login(String email, String password) async {
    final uri = Uri.parse(baseUrl + "/login").replace(queryParameters: {
      'email': email,
      'password': password,
    });
    final response = await http.post(
      uri,
      headers: {'Accept': 'application/json'},
    );
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      final token = data['token'];
      if (token != null) {
        final prefs = await SharedPreferences.getInstance();
        await prefs.setString('token', token);
        return null;
      }
      return 'Không nhận được token';
    } else {
      try {
        final data = json.decode(response.body);
        return data['message'] ?? 'Đăng nhập thất bại';
      } catch (e) {
        return response.body;
      }
    }
  }

  // Đăng ký ứng viên
  static Future<String?> registerCandidate(String fullname, String email, String password) async {
    final uri = Uri.parse(baseUrl + "/candidate/register").replace(queryParameters: {'password': password});
    final response = await http.post(
      uri,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'fullname': fullname,
        'email': email,
      }),
    );
    if (response.statusCode == 200) {
      return null;
    } else {
      try {
        final data = json.decode(response.body);
        return data['message'] ?? 'Đăng ký thất bại';
      } catch (e) {
        return response.body;
      }
    }
  }

  // Lấy danh sách thông báo của người dùng
  static Future<List<dynamic>> getNotifications(int userId) async {
    final response = await http.get(Uri.parse(
        baseUrl + "/notifications/" + userId.toString()));
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Failed to load notifications');
    }
  }

  // Đánh dấu một thông báo là đã đọc
  static Future<void> markNotificationAsRead(int notificationId) async {
    final response = await http.patch(Uri.parse(
        baseUrl + "/notifications/" + notificationId.toString() + "/read"));
    if (response.statusCode != 204) {
      throw Exception('Failed to mark notification as read');
    }
  }

  // Lấy danh sách việc làm
  static Future<List<dynamic>?> getJobs({String? params, String? token}) async {
    final uri = Uri.parse(baseUrl + "/jobs" + (params != null && params.isNotEmpty ? "?$params" : ""));
    final response = await http.get(
      uri,
      headers: token != null ? { 'Authorization': 'Bearer $token' } : {},
    );
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Failed to load jobs: ' + response.body);
    }
  }

  // Lấy chi tiết việc làm
  static Future<Map<String, dynamic>?> getJobById(String jobId, {String? token}) async {
    final uri = Uri.parse(baseUrl + "/jobs/$jobId");
    final response = await http.get(
      uri,
      headers: token != null ? { 'Authorization': 'Bearer $token' } : {},
    );
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Failed to load job detail: ' + response.body);
    }
  }

  // Nộp đơn ứng tuyển
  static Future<String?> applyJob(String jobId, String resumeId, {String? token}) async {
    final uri = Uri.parse(baseUrl + "/applications/$jobId").replace(queryParameters: { 'resumeId': resumeId });
    final response = await http.post(
      uri,
      headers: token != null ? { 'Authorization': 'Bearer $token' } : {},
    );
    if (response.statusCode == 200 || response.statusCode == 201) {
      return null;
    } else {
      try {
        final data = json.decode(response.body);
        return data['message'] ?? 'Nộp đơn thất bại';
      } catch (e) {
        return response.body;
      }
    }
  }

  // Lấy danh sách CV của user
  static Future<List<dynamic>?> getUserResumes({String? token}) async {
    final uri = Uri.parse(baseUrl + "/resumes/me");
    final response = await http.get(
      uri,
      headers: token != null ? { 'Authorization': 'Bearer $token' } : {},
    );
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Failed to load resumes: ' + response.body);
    }
  }

  // Lấy thông tin profile ứng viên
  static Future<Map<String, dynamic>?> getProfile({String? token}) async {
    final uri = Uri.parse(baseUrl + "/users/me");
    final response = await http.get(
      uri,
      headers: token != null ? { 'Authorization': 'Bearer $token' } : {},
    );
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Failed to load profile: ' + response.body);
    }
  }

  // Upload CV (resume) file
  static Future<void> uploadCV(File file, {String? token}) async {
    final uri = Uri.parse(baseUrl + "/resumes/upload");
    final request = http.MultipartRequest('POST', uri);
    request.headers['Accept'] = 'application/json';
    if (token != null) {
      request.headers['Authorization'] = 'Bearer $token';
    }
    request.files.add(await http.MultipartFile.fromPath('file', file.path));
    final streamedResponse = await request.send();
    final response = await http.Response.fromStream(streamedResponse);
    if (response.statusCode != 200 && response.statusCode != 201) {
      throw Exception('Tải lên thất bại: ' + response.body);
    }
  }
}
