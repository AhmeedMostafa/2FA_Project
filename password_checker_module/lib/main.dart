import 'package:flutter/material.dart';

void main() {
  runApp(const PasswordCheckerApp());
}

class PasswordCheckerApp extends StatelessWidget {
  const PasswordCheckerApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.blue,
        useMaterial3: true,
      ),
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
  bool _isObscured = true;
  final TextEditingController _controller = TextEditingController(); // عشان نقدر نغير النص برمجياً

  double get _strength {
    double strength = 0;
    if (_password.length > 6) strength += 0.25;
    if (_password.contains(RegExp(r'[A-Z]'))) strength += 0.25;
    if (_password.contains(RegExp(r'[0-9]'))) strength += 0.25;
    if (_password.contains(RegExp(r'[!@#$%^&*(),.?":{}|<>]'))) strength += 0.25;
    return strength;
  }

  // 2. Logic: تحديد اللون
  Color get _strengthColor {
    if (_strength <= 0.25) return Colors.red;
    if (_strength <= 0.5) return Colors.orange;
    if (_strength <= 0.75) return Colors.blue;
    return Colors.green;
  }

  // 3. Logic: تحديد النص
  String get _strengthText {
    if (_password.isEmpty) return 'Enter Password';
    if (_strength <= 0.25) return 'Weak 🔴';
    if (_strength <= 0.5) return 'Medium 🟠';
    if (_strength <= 0.75) return 'Good 🔵';
    return 'Strong 🟢';
  }

  // 4. Logic: توليد اقتراحات ذكية (New Feature 🚀)
  List<String> get _suggestions {
    if (_password.isEmpty || _strength == 1.0) return []; // لو قوية أو فاضية مطلعش حاجة

    List<String> suggestions = [];
    String base = _password.trim();
    if (base.isEmpty) base = "Pass"; // كلمة افتراضية لو مسح كله

    // الاقتراح الأول: تكبير الحرف الأول + سنة + رمز
    suggestions.add('${base[0].toUpperCase()}${base.substring(1)}@2025');

    // الاقتراح الثاني: استبدال الحروف بأرقام (Leet Speak)
    String leet = base
        .replaceAll('a', '@')
        .replaceAll('o', '0')
        .replaceAll('i', '1')
        .replaceAll('s', '\$');
    suggestions.add('${leet}#Strong!');

    // الاقتراح الثالث: إضافة رمز عشوائي في الآخر
    suggestions.add('${base}#X9!');

    return suggestions;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Password Strength Checker"),
        backgroundColor: Colors.blue,
        foregroundColor: Colors.white,
      ),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const Text(
                "Check your password security:",
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 20),

              // --- Input Field ---
              TextField(
                controller: _controller, // ربطنا الـ Controller
                onChanged: (value) => setState(() => _password = value),
                obscureText: _isObscured,
                decoration: InputDecoration(
                  border: const OutlineInputBorder(),
                  labelText: 'Type Password Here',
                  prefixIcon: const Icon(Icons.lock),
                  suffixIcon: IconButton(
                    icon: Icon(_isObscured ? Icons.visibility : Icons.visibility_off),
                    onPressed: () {
                      setState(() {
                        _isObscured = !_isObscured;
                      });
                    },
                  ),
                ),
              ),
              const SizedBox(height: 20),

              // --- Progress Bar ---
              ClipRRect(
                borderRadius: BorderRadius.circular(10),
                child: LinearProgressIndicator(
                  value: _strength == 0 ? 0.05 : _strength,
                  backgroundColor: Colors.grey[300],
                  color: _strengthColor,
                  minHeight: 15,
                ),
              ),
              const SizedBox(height: 10),

              Text(
                _strengthText,
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: _strengthColor,
                ),
              ),

              // --- Suggestions Section (New) ---
              if (_suggestions.isNotEmpty) ...[
                const SizedBox(height: 20),
                const Text(
                  "✨ Better Suggestions (Click to apply):",
                  style: TextStyle(fontWeight: FontWeight.bold, color: Colors.blueGrey),
                ),
                const SizedBox(height: 10),
                Wrap(
                  spacing: 8.0,
                  children: _suggestions.map((suggestion) {
                    return ActionChip(
                      label: Text(suggestion),
                      backgroundColor: Colors.blue.shade50,
                      avatar: const Icon(Icons.bolt, color: Colors.orange, size: 18),
                      onPressed: () {
                        // لما يدوس، نغير الباسورد للي هو اختاره
                        setState(() {
                          _password = suggestion;
                          _controller.text = suggestion; // تحديث النص في الخانة
                          _controller.selection = TextSelection.fromPosition(
                              TextPosition(offset: _controller.text.length)); // المؤشر في الآخر
                        });
                      },
                    );
                  }).toList(),
                ),
              ],

              const SizedBox(height: 30),
              const Divider(),
              const SizedBox(height: 10),

              // --- Checklist ---
              _buildRequirement("At least 6 characters", _password.length > 6),
              _buildRequirement("Contains Uppercase Letter (A-Z)", _password.contains(RegExp(r'[A-Z]'))),
              _buildRequirement("Contains Number (0-9)", _password.contains(RegExp(r'[0-9]'))),
              _buildRequirement("Contains Symbol (@#\$)", _password.contains(RegExp(r'[!@#$%^&*(),.?":{}|<>]'))),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildRequirement(String text, bool met) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 5),
      child: Row(
        children: [
          Icon(
            met ? Icons.check_circle : Icons.circle_outlined,
            color: met ? Colors.green : Colors.grey[400],
            size: 20,
          ),
          const SizedBox(width: 10),
          Text(
            text,
            style: TextStyle(
              color: met ? Colors.black87 : Colors.grey[500],
              decoration: met ? TextDecoration.none : TextDecoration.none, // شيلنا الخط عشان الشكل يبقى أنضف
            ),
          ),
        ],
      ),
    );
  }
}
