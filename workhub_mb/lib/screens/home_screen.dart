import 'package:flutter/material.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F7FC),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        title: const Text('WorkHub', style: TextStyle(color: Color(0xFF202124), fontWeight: FontWeight.bold)),
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
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(24),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.04),
                    blurRadius: 12,
                    offset: const Offset(0, 4),
                  ),
                ],
              ),
              child: Column(
                children: const [
                  Icon(Icons.work_outline_rounded, size: 48, color: Color(0xFF1967D2)),
                  SizedBox(height: 16),
                  Text('Chào mừng đến với WorkHub!', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                  SizedBox(height: 8),
                  Text('Tìm kiếm việc làm, quản lý hồ sơ và nhiều hơn nữa.', textAlign: TextAlign.center, style: TextStyle(color: Color(0xFF7A7A7A))),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
