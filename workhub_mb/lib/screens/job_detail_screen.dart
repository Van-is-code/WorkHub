import 'package:flutter/material.dart';
import '../services/api_service.dart';
import 'package:shared_preferences/shared_preferences.dart';

class JobDetailScreen extends StatefulWidget {
  final String jobId;
  const JobDetailScreen({Key? key, required this.jobId}) : super(key: key);

  @override
  State<JobDetailScreen> createState() => _JobDetailScreenState();
}

class _JobDetailScreenState extends State<JobDetailScreen> {
  Map<String, dynamic>? job;
  bool loading = true;
  String error = '';
  bool applying = false;
  String applyResult = '';

  List<dynamic>? resumes;
  String selectedResumeId = '';
  bool loadingResumes = true;

  @override
  void initState() {
    super.initState();
    fetchJobDetail();
    fetchResumes();
  }

  Future<void> fetchJobDetail() async {
    setState(() { loading = true; error = ''; });
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('token');
      final data = await ApiService.getJobById(widget.jobId, token: token);
      setState(() {
        job = data;
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

  Future<void> applyJob() async {
    if (selectedResumeId.isEmpty) return;
    setState(() { applying = true; applyResult = ''; });
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('token');
      final result = await ApiService.applyJob(widget.jobId, selectedResumeId, token: token);
      setState(() {
        applyResult = result == null ? 'Nộp đơn thành công!' : result;
        applying = false;
      });
    } catch (e) {
      setState(() {
        applyResult = e.toString();
        applying = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F7FC),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        title: const Text('Chi tiết việc làm', style: TextStyle(color: Color(0xFF202124), fontWeight: FontWeight.bold)),
        centerTitle: true,
        iconTheme: const IconThemeData(color: Color(0xFF202124)),
        actions: [
          IconButton(
            icon: const Icon(Icons.person_outline_rounded, color: Color(0xFF1967D2)),
            tooltip: 'Hồ sơ',
            onPressed: () {
              Navigator.pushNamed(context, '/profile');
            },
          ),
        ],
      ),
      body: loading
          ? const Center(child: CircularProgressIndicator())
          : error.isNotEmpty
              ? Center(child: Text(error, style: const TextStyle(color: Colors.red)))
              : job == null
                  ? const Center(child: Text('Không tìm thấy việc làm'))
                  : SingleChildScrollView(
                      child: Padding(
                        padding: const EdgeInsets.all(16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Card(
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
                              elevation: 2,
                              child: Padding(
                                padding: const EdgeInsets.all(20),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Row(
                                      children: [
                                        CircleAvatar(
                                          backgroundColor: const Color(0xFFE3EAF2),
                                          child: const Icon(Icons.work_outline_rounded, color: Color(0xFF1967D2)),
                                        ),
                                        const SizedBox(width: 16),
                                        Expanded(
                                          child: Column(
                                            crossAxisAlignment: CrossAxisAlignment.start,
                                            children: [
                                              Text(job!['title'] ?? '', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 20)),
                                              if (job!['companyName'] != null)
                                                Text(job!['companyName'], style: const TextStyle(color: Color(0xFF7A7A7A))),
                                            ],
                                          ),
                                        ),
                                      ],
                                    ),
                                    const SizedBox(height: 16),
                                    if (job!['location'] != null)
                                      Row(
                                        children: [
                                          const Icon(Icons.location_on, size: 18, color: Color(0xFF7A7A7A)),
                                          const SizedBox(width: 4),
                                          Text(job!['location'], style: const TextStyle(color: Color(0xFF7A7A7A))),
                                        ],
                                      ),
                                    if (job!['salary'] != null) ...[
                                      const SizedBox(height: 8),
                                      Row(
                                        children: [
                                          const Icon(Icons.attach_money, size: 18, color: Color(0xFF7A7A7A)),
                                          const SizedBox(width: 4),
                                          Text(job!['salary'].toString(), style: const TextStyle(color: Color(0xFF7A7A7A))),
                                        ],
                                      ),
                                    ],
                                    if (job!['description'] != null) ...[
                                      const SizedBox(height: 16),
                                      const Text('Mô tả công việc', style: TextStyle(fontWeight: FontWeight.bold)),
                                      const SizedBox(height: 6),
                                      Text(job!['description'], style: const TextStyle(color: Color(0xFF202124))),
                                    ],
                                  ],
                                ),
                              ),
                            ),
                            const SizedBox(height: 24),
                            Card(
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
                              elevation: 2,
                              child: Padding(
                                padding: const EdgeInsets.all(20),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    const Text('Nộp đơn ứng tuyển', style: TextStyle(fontWeight: FontWeight.bold)),
                                    const SizedBox(height: 12),
                                    loadingResumes
                                        ? const Center(child: CircularProgressIndicator())
                                        : resumes == null || resumes!.isEmpty
                                            ? const Text('Bạn chưa có hồ sơ nào.')
                                            : DropdownButtonFormField<String>(
                                                value: selectedResumeId.isNotEmpty ? selectedResumeId : null,
                                                items: resumes!.map<DropdownMenuItem<String>>((resume) {
                                                  return DropdownMenuItem<String>(
                                                    value: resume['id'].toString(),
                                                    child: Text(resume['title'] ?? 'CV'),
                                                  );
                                                }).toList(),
                                                onChanged: (value) {
                                                  setState(() {
                                                    selectedResumeId = value ?? '';
                                                  });
                                                },
                                                decoration: const InputDecoration(
                                                  labelText: 'Chọn hồ sơ',
                                                  border: OutlineInputBorder(),
                                                ),
                                              ),
                                    const SizedBox(height: 16),
                                    SizedBox(
                                      width: double.infinity,
                                      child: ElevatedButton(
                                        onPressed: applying || selectedResumeId.isEmpty ? null : applyJob,
                                        style: ElevatedButton.styleFrom(
                                          backgroundColor: const Color(0xFF1967D2),
                                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                                        ),
                                        child: applying
                                            ? const SizedBox(
                                                width: 20,
                                                height: 20,
                                                child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                                              )
                                            : const Text('Nộp đơn'),
                                      ),
                                    ),
                                    if (applyResult.isNotEmpty) ...[
                                      const SizedBox(height: 8),
                                      Text(applyResult, style: TextStyle(color: Color(0xFF1967D2))),
                                    ],
                                  ],
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
    );
  }
}
