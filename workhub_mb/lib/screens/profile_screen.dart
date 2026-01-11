import 'package:flutter/material.dart';
import '../services/api_service.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:file_picker/file_picker.dart';
import 'dart:io';
import 'package:url_launcher/url_launcher.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({Key? key}) : super(key: key);

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  Map<String, dynamic>? user;
  bool loading = true;
  String error = '';
  List<dynamic>? resumes;
  bool loadingResumes = true;
  String uploadMsg = '';

  @override
  void initState() {
    super.initState();
    fetchProfile();
    fetchResumes();
  }

  Future<void> fetchProfile() async {
    setState(() { loading = true; error = ''; });
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('token');
      final data = await ApiService.getProfile(token: token);
      setState(() {
        user = data;
        loading = false;
      });
    } catch (e) {
      setState(() {
        error = e.toString();
        loading = false;
      });
    }
  }

  Future<void> fetchResumes() async {
    setState(() { loadingResumes = true; });
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('token');
      final data = await ApiService.getUserResumes(token: token);
      setState(() {
        resumes = data;
        loadingResumes = false;
      });
    } catch (e) {
      setState(() { loadingResumes = false; });
    }
  }

  Future<void> uploadCV() async {
    setState(() { uploadMsg = ''; });
    FilePickerResult? result = await FilePicker.platform.pickFiles(type: FileType.custom, allowedExtensions: ['pdf', 'doc', 'docx']);
    if (result != null && result.files.single.path != null) {
      File file = File(result.files.single.path!);
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('token');
      try {
        await ApiService.uploadCV(file, token: token);
        setState(() { uploadMsg = 'Tải lên thành công!'; });
        fetchResumes();
      } catch (e) {
        setState(() { uploadMsg = 'Tải lên thất bại: $e'; });
      }
    }
  }

  void openCV(String url) async {
    debugPrint('openCV url: $url');
    final uri = Uri.parse(url);
    if (await canLaunchUrl(uri)) {
      await launchUrl(uri, mode: LaunchMode.externalApplication);
    } else {
      debugPrint('Cannot launch url: $url');
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Không thể mở file.')));
    }
  }

  void downloadCV(String url) async {
    debugPrint('downloadCV url: $url');
    openCV(url);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F7FC),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        title: const Text('Hồ sơ cá nhân', style: TextStyle(color: Color(0xFF202124), fontWeight: FontWeight.bold)),
        centerTitle: true,
        iconTheme: const IconThemeData(color: Color(0xFF202124)),
        actions: [
          Padding(
            padding: const EdgeInsets.only(right: 12),
            child: user != null ? GestureDetector(
              onTap: () {},
              child: CircleAvatar(
                radius: 20,
                backgroundColor: const Color(0xFFE3EAF2),
                backgroundImage: user!['avatarUrl'] != null ? NetworkImage(user!['avatarUrl']) : null,
                child: user!['avatarUrl'] == null ? const Icon(Icons.person, color: Color(0xFFB0B0B0)) : null,
              ),
            ) : null,
          ),
        ],
      ),
      body: loading
          ? const Center(child: CircularProgressIndicator())
          : error.isNotEmpty
              ? Center(child: Text(error, style: const TextStyle(color: Colors.red)))
              : user == null
                  ? const Center(child: Text('Không tìm thấy thông tin người dùng'))
                  : SingleChildScrollView(
                      child: Column(
                        children: [
                          Container(
                            width: double.infinity,
                            padding: const EdgeInsets.symmetric(vertical: 32),
                            color: Colors.white,
                            child: Column(
                              children: [
                                CircleAvatar(
                                  radius: 54,
                                  backgroundColor: const Color(0xFFE3EAF2),
                                  backgroundImage: user!['avatarUrl'] != null
                                      ? NetworkImage(user!['avatarUrl'])
                                      : null,
                                  child: user!['avatarUrl'] == null
                                      ? const Icon(Icons.person, size: 54, color: Color(0xFFB0B0B0))
                                      : null,
                                ),
                                const SizedBox(height: 16),
                                Text(
                                  user!['fullname'] ?? '',
                                  style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: Color(0xFF202124)),
                                ),
                                const SizedBox(height: 6),
                                Text(
                                  user!['email'] ?? '',
                                  style: const TextStyle(fontSize: 16, color: Color(0xFF7A7A7A)),
                                ),
                                if (user!['phone'] != null) ...[
                                  const SizedBox(height: 6),
                                  Text(user!['phone'], style: const TextStyle(fontSize: 16, color: Color(0xFF7A7A7A))),
                                ],
                                const SizedBox(height: 16),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    IconButton(
                                      icon: const Icon(Icons.edit, color: Color(0xFF1967D2)),
                                      onPressed: () {},
                                      tooltip: 'Chỉnh sửa',
                                    ),
                                    IconButton(
                                      icon: const Icon(Icons.settings, color: Color(0xFF1967D2)),
                                      onPressed: () {},
                                      tooltip: 'Cài đặt',
                                    ),
                                    IconButton(
                                      icon: const Icon(Icons.logout, color: Color(0xFFFF4D4F)),
                                      onPressed: () {},
                                      tooltip: 'Đăng xuất',
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                          const SizedBox(height: 16),
                          Padding(
                            padding: const EdgeInsets.symmetric(horizontal: 16),
                            child: Card(
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
                              elevation: 2,
                              child: Padding(
                                padding: const EdgeInsets.all(20),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    const Text('Thông tin cá nhân', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                                    const SizedBox(height: 12),
                                    _infoRow(Icons.email, 'Email', user!['email'] ?? ''),
                                    if (user!['phone'] != null) _infoRow(Icons.phone, 'Số điện thoại', user!['phone']),
                                    if (user!['address'] != null) _infoRow(Icons.location_on, 'Địa chỉ', user!['address']),
                                  ],
                                ),
                              ),
                            ),
                          ),
                          const SizedBox(height: 24),
                          Padding(
                            padding: const EdgeInsets.symmetric(horizontal: 16),
                            child: Card(
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
                              elevation: 2,
                              child: Padding(
                                padding: const EdgeInsets.all(20),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Row(
                                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      children: [
                                        const Text('Quản lý CV', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                                        ElevatedButton.icon(
                                          onPressed: uploadCV,
                                          icon: const Icon(Icons.upload_file),
                                          label: const Text('Tải lên CV'),
                                          style: ElevatedButton.styleFrom(
                                            backgroundColor: const Color(0xFF1967D2),
                                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                                            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                                          ),
                                        ),
                                      ],
                                    ),
                                    if (uploadMsg.isNotEmpty) ...[
                                      const SizedBox(height: 8),
                                      Text(uploadMsg, style: TextStyle(color: uploadMsg.contains('thành công') ? Colors.green : Colors.red)),
                                    ],
                                    const SizedBox(height: 12),
                                    loadingResumes
                                        ? const Center(child: CircularProgressIndicator())
                                        : resumes == null || resumes!.isEmpty
                                            ? const Text('Bạn chưa có CV nào.')
                                            : ListView.builder(
                                                shrinkWrap: true,
                                                physics: const NeverScrollableScrollPhysics(),
                                                itemCount: resumes!.length,
                                                itemBuilder: (context, idx) {
                                                  final cv = resumes![idx];
                                                  return Card(
                                                    margin: const EdgeInsets.symmetric(vertical: 6),
                                                    child: ListTile(
                                                      leading: const Icon(Icons.description, color: Color(0xFF1967D2)),
                                                      title: Text(cv['title'] ?? 'CV'),
                                                      subtitle: Text(cv['createdAt'] ?? ''),
                                                      trailing: Row(
                                                        mainAxisSize: MainAxisSize.min,
                                                        children: [
                                                          IconButton(
                                                            icon: const Icon(Icons.visibility, color: Color(0xFF1967D2)),
                                                            onPressed: () => openCV(cv['fileUrl']),
                                                            tooltip: 'Xem CV',
                                                          ),
                                                          IconButton(
                                                            icon: const Icon(Icons.download, color: Color(0xFF1967D2)),
                                                            onPressed: () => downloadCV(cv['fileUrl']),
                                                            tooltip: 'Tải CV',
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                  );
                                                },
                                              ),
                                  ],
                                ),
                              ),
                            ),
                          ),
                          const SizedBox(height: 24),
                        ],
                      ),
                    ),
    );
  }

  Widget _infoRow(IconData icon, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        children: [
          Icon(icon, color: const Color(0xFF1967D2), size: 20),
          const SizedBox(width: 12),
          Text(label + ': ', style: const TextStyle(fontWeight: FontWeight.w500)),
          Expanded(child: Text(value, style: const TextStyle(color: Color(0xFF7A7A7A)))),
        ],
      ),
    );
  }
}
