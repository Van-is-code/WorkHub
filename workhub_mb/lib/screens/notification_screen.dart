import 'package:flutter/material.dart';
import '../services/api_service.dart';

class NotificationScreen extends StatefulWidget {
  const NotificationScreen({Key? key}) : super(key: key);

  @override
  State<NotificationScreen> createState() => _NotificationScreenState();
}

class _NotificationScreenState extends State<NotificationScreen> {
  late Future<List<dynamic>> notifications;

  @override
  void initState() {
    super.initState();
    // TODO: Thay userId này bằng userId thực tế khi đã có đăng nhập
    int userId = 1;
    notifications = ApiService.getNotifications(userId);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Notifications')),
      body: FutureBuilder<List<dynamic>>(
        future: notifications,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: \\${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return const Center(child: Text('No notifications found.'));
          }
          return ListView.builder(
            itemCount: snapshot.data!.length,
            itemBuilder: (context, index) {
              final notification = snapshot.data![index];
              return ListTile(
                title: Text(notification['title'] ?? 'No title'),
                subtitle: Text(notification['body'] ?? ''),
              );
            },
          );
        },
      ),
    );
  }
}
