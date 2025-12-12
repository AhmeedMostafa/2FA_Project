import 'package:flutter/material.dart';

void main() { runApp(const PasswordCheckerApp()); }

class PasswordCheckerApp extends StatelessWidget {
  const PasswordCheckerApp({super.key});
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(primarySwatch: Colors.blue, useMaterial3: true),
      home: const PasswordStrengthScreen(),
    );
  }
}

class PasswordStrengthScreen extends StatefulWidget {
  const PasswordStrengthScreen({super.key});
  @override
  State<PasswordStrengthScreen> createState() => _PasswordStrengthScreenState();
}

class _PasswordStrengthScreenState extends State<PasswordStrengthScreen> {
  String _password = '';

  double get _strength {
    double strength = 0;
    if (_password.length > 6) strength += 0.25;
    if (_password.contains(RegExp(r'[A-Z]'))) strength += 0.25;
    if (_password.contains(RegExp(r'[0-9]'))) strength += 0.25;
    if (_password.contains(RegExp(r'[!@#$%^&*(),.?":{}|<>]'))) strength += 0.25;
    return strength;
  }

  Color get _strengthColor {
    if (_strength <= 0.25) return Colors.red;
    if (_strength <= 0.5) return Colors.orange;
    if (_strength <= 0.75) return Colors.blue;
    return Colors.green;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Password Strength Checker")),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          children: [
            TextField(
              onChanged: (value) => setState(() => _password = value),
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                labelText: 'Type Password Here',
                prefixIcon: Icon(Icons.lock),
              ),
            ),
            const SizedBox(height: 20),
            LinearProgressIndicator(
              value: _strength,
              backgroundColor: Colors.grey[300],
              color: _strengthColor,
              minHeight: 15,
            ),
            const SizedBox(height: 10),
            Text("Score: ${(_strength * 100).toInt()}%"),
          ],
        ),
      ),
    );
  }
}