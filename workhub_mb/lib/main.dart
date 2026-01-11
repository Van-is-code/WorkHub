import 'package:flutter/material.dart';
import 'screens/home_screen.dart';
import 'screens/notification_screen.dart';
import 'screens/login_screen.dart';
import 'screens/register_screen.dart';
import 'screens/job_list_screen.dart';
import 'screens/profile_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'WorkHub',
      theme: workhubTheme,
      debugShowCheckedModeBanner: false,
      initialRoute: '/login',
      routes: {
        '/login': (context) => const LoginScreen(),
        '/register': (context) => const RegisterScreen(),
        '/': (context) => const JobListScreen(),
        '/profile': (context) => const ProfileScreen(),
        // Thêm các route khác nếu cần
      },
    );
  }
}

final ThemeData workhubTheme = ThemeData(
  primaryColor: const Color(0xFF1967D2), // primary
  scaffoldBackgroundColor: const Color(0xFFF5F7FC), // secondary
  colorScheme: ColorScheme.fromSwatch().copyWith(
    primary: const Color(0xFF1967D2),
    secondary: const Color(0xFFFFB200), // accent
    background: const Color(0xFFF5F7FC),
    error: const Color(0xFFFF4D4F), // danger
  ),
  textTheme: const TextTheme(
    bodyMedium: TextStyle(color: Color(0xFF202124)), // dark
    bodySmall: TextStyle(color: Color(0xFF7A7A7A)), // muted
  ),
  fontFamily: 'Poppins',
  elevatedButtonTheme: ElevatedButtonThemeData(
    style: ElevatedButton.styleFrom(
      backgroundColor: const Color(0xFF1967D2),
      foregroundColor: Colors.white,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
      ),
      textStyle: const TextStyle(fontFamily: 'Poppins'),
    ),
  ),
);
