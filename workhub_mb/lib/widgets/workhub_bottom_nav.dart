import 'package:flutter/material.dart';

class WorkHubBottomNav extends StatelessWidget {
  final int currentIndex;
  final Function(int) onTap;
  const WorkHubBottomNav({Key? key, required this.currentIndex, required this.onTap}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BottomNavigationBar(
      currentIndex: currentIndex,
      onTap: onTap,
      type: BottomNavigationBarType.fixed,
      backgroundColor: Colors.white,
      selectedItemColor: const Color(0xFF1967D2),
      unselectedItemColor: const Color(0xFF7A7A7A),
      showUnselectedLabels: true,
      items: const [
        BottomNavigationBarItem(
          icon: Icon(Icons.home_rounded),
          label: 'Trang chủ',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.work_outline_rounded),
          label: 'Việc làm',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.notifications_none_rounded),
          label: 'Thông báo',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.person_outline_rounded),
          label: 'Hồ sơ',
        ),
      ],
    );
  }
}
