import 'package:flutter/material.dart';
import '../services/api_service.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({Key? key}) : super(key: key);

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  String fullname = '';
  String email = '';
  String password = '';
  String confirmPassword = '';
  String error = '';
  bool loading = false;

  void _register() async {
    if (!_formKey.currentState!.validate()) return;
    if (password != confirmPassword) {
      setState(() { error = 'Mật khẩu xác nhận không khớp!'; });
      return;
    }
    setState(() { loading = true; error = ''; });
    final err = await ApiService.registerCandidate(fullname, email, password);
    setState(() { loading = false; });
    if (err == null) {
      showDialog(
        context: context,
        builder: (ctx) => AlertDialog(
          title: const Text('Thành công'),
          content: const Text('Đăng ký thành công! Vui lòng đăng nhập.'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.pop(ctx);
                Navigator.pushReplacementNamed(context, '/login');
              },
              child: const Text('Đăng nhập'),
            ),
          ],
        ),
      );
    } else {
      setState(() { error = err; });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F7FC),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: Card(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
            elevation: 4,
            child: Padding(
              padding: const EdgeInsets.all(28),
              child: Form(
                key: _formKey,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const SizedBox(height: 8),
                    const Icon(Icons.person_add_alt_1_rounded, size: 48, color: Color(0xFF1967D2)),
                    const SizedBox(height: 16),
                    const Text('Đăng ký tài khoản', style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 24),
                    TextFormField(
                      decoration: const InputDecoration(
                        labelText: 'Họ và tên',
                        prefixIcon: Icon(Icons.person_outline),
                        border: OutlineInputBorder(),
                      ),
                      validator: (value) => value == null || value.isEmpty ? 'Vui lòng nhập họ tên' : null,
                      onChanged: (value) => fullname = value,
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      decoration: const InputDecoration(
                        labelText: 'Email',
                        prefixIcon: Icon(Icons.email_outlined),
                        border: OutlineInputBorder(),
                      ),
                      keyboardType: TextInputType.emailAddress,
                      validator: (value) => value == null || value.isEmpty ? 'Vui lòng nhập email' : null,
                      onChanged: (value) => email = value,
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      decoration: const InputDecoration(
                        labelText: 'Mật khẩu',
                        prefixIcon: Icon(Icons.lock_outline),
                        border: OutlineInputBorder(),
                      ),
                      obscureText: true,
                      validator: (value) => value == null || value.isEmpty ? 'Vui lòng nhập mật khẩu' : null,
                      onChanged: (value) => password = value,
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      decoration: const InputDecoration(
                        labelText: 'Xác nhận mật khẩu',
                        prefixIcon: Icon(Icons.lock_outline),
                        border: OutlineInputBorder(),
                      ),
                      obscureText: true,
                      validator: (value) => value == null || value.isEmpty ? 'Vui lòng xác nhận mật khẩu' : null,
                      onChanged: (value) => confirmPassword = value,
                    ),
                    const SizedBox(height: 20),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton(
                        onPressed: loading ? null : _register,
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF1967D2),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                        ),
                        child: loading
                            ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
                            : const Text('Đăng ký'),
                      ),
                    ),
                    if (error.isNotEmpty) ...[
                      const SizedBox(height: 12),
                      Text(error, style: const TextStyle(color: Color(0xFFFF4D4F))),
                    ],
                    const SizedBox(height: 16),
                    TextButton(
                      onPressed: () {
                        Navigator.pushReplacementNamed(context, '/login');
                      },
                      child: const Text('Đã có tài khoản? Đăng nhập!'),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
