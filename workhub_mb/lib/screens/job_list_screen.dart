import 'package:flutter/material.dart';
import '../services/api_service.dart';
import 'job_detail_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';

class JobListScreen extends StatefulWidget {
  const JobListScreen({Key? key}) : super(key: key);

  @override
  State<JobListScreen> createState() => _JobListScreenState();
}

class _JobListScreenState extends State<JobListScreen> {
  List<dynamic>? jobs;
  bool loading = true;
  String error = '';

  @override
  void initState() {
    super.initState();
    fetchJobs();
  }

  Future<void> fetchJobs() async {
    setState(() { loading = true; error = ''; });
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('token');
      final data = await ApiService.getJobs(token: token);
      setState(() {
        jobs = data;
        loading = false;
      });
    } catch (e) {
      setState(() {
        error = e.toString();
        loading = false;
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
        title: const Text('Việc làm', style: TextStyle(color: Color(0xFF202124), fontWeight: FontWeight.bold)),
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
              : jobs == null || jobs!.isEmpty
                  ? const Center(child: Text('Không có việc làm nào'))
                  : ListView.builder(
                      padding: const EdgeInsets.all(16),
                      itemCount: jobs!.length,
                      itemBuilder: (context, index) {
                        final job = jobs![index];
                        return Card(
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
                          elevation: 2,
                          margin: const EdgeInsets.only(bottom: 16),
                          child: ListTile(
                            contentPadding: const EdgeInsets.symmetric(vertical: 16, horizontal: 20),
                            leading: CircleAvatar(
                              backgroundColor: const Color(0xFFE3EAF2),
                              child: const Icon(Icons.work_outline_rounded, color: Color(0xFF1967D2)),
                            ),
                            title: Text(job['title'] ?? '', style: const TextStyle(fontWeight: FontWeight.bold)),
                            subtitle: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                if (job['companyName'] != null)
                                  Text(job['companyName'], style: const TextStyle(color: Color(0xFF7A7A7A))),
                                if (job['location'] != null)
                                  Row(
                                    children: [
                                      const Icon(Icons.location_on, size: 16, color: Color(0xFF7A7A7A)),
                                      const SizedBox(width: 4),
                                      Text(job['location'], style: const TextStyle(color: Color(0xFF7A7A7A))),
                                    ],
                                  ),
                              ],
                            ),
                            trailing: const Icon(Icons.arrow_forward_ios_rounded, size: 18, color: Color(0xFFB0B0B0)),
                            onTap: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (context) => JobDetailScreen(jobId: job['id'].toString()),
                                ),
                              );
                            },
                          ),
                        );
                      },
                    ),
    );
  }
}
