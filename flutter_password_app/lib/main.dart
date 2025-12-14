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
      title: 'Password Checker',
      
      theme: ThemeData(
        brightness: Brightness.dark,
        scaffoldBackgroundColor: const Color(0xFF1E1E1E), 
        
        colorScheme: const ColorScheme.dark(
          primary: Color(0xFF3B82F6), 
          secondary: Color(0xFF22C55E), 
          surface: Color(0xFF252526),
        ),

        appBarTheme: const AppBarTheme(
          backgroundColor: Color(0xFF1E1E1E),
          elevation: 0,
          centerTitle: true,
        ),

        inputDecorationTheme: InputDecorationTheme(
          filled: true,
          fillColor: const Color(0xFF252526),
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: BorderSide.none,
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: const BorderSide(color: Colors.grey),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: const BorderSide(color: Color(0xFF3B82F6)), 
          ),
          labelStyle: const TextStyle(color: Colors.white70),
          prefixIconColor: Colors.white70,
          suffixIconColor: Colors.white70,
        ),
        
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
  final TextEditingController _controller = TextEditingController();

  double get _strength {
    double strength = 0;
    if (_password.length > 6) strength += 0.25;
    if (_password.contains(RegExp(r'[A-Z]'))) strength += 0.25;
    if (_password.contains(RegExp(r'[0-9]'))) strength += 0.25;
    if (_password.contains(RegExp(r'[!@#$%^&*(),.?":{}|<>]'))) strength += 0.25;
    return strength;
  }

  Color get _strengthColor {
    if (_strength <= 0.25) return Colors.redAccent; 
    if (_strength <= 0.5) return Colors.orangeAccent;
    if (_strength <= 0.75) return Colors.blueAccent;
    return const Color(0xFF22C55E); 
  }

  String get _strengthText {
    if (_password.isEmpty) return 'Enter Password';
    if (_strength <= 0.25) return 'Weak 🔴';
    if (_strength <= 0.5) return 'Medium 🟠';
    if (_strength <= 0.75) return 'Good 🔵';
    return 'Strong 🟢';
  }

  List<String> get _suggestions {
    if (_password.isEmpty || _strength == 1.0) return [];

    List<String> suggestions = [];
    String base = _password.trim();
    if (base.isEmpty) base = "Pass";

    suggestions.add('${base[0].toUpperCase()}${base.substring(1)}@2025');

    String leet = base
        .replaceAll('a', '@')
        .replaceAll('o', '0')
        .replaceAll('i', '1')
        .replaceAll('s', '\$');
    suggestions.add('${leet}#Strong!');

    suggestions.add('${base}#X9!');

    return suggestions;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Password Security"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const Text(
                "Check your password security:",
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.white),
              ),
              const SizedBox(height: 20),

              TextField(
                controller: _controller,
                onChanged: (value) => setState(() => _password = value),
                obscureText: _isObscured,
                style: const TextStyle(color: Colors.white),
                decoration: InputDecoration(
                  labelText: 'Type Password Here',
                  prefixIcon: const Icon(Icons.lock_outline),
                  suffixIcon: IconButton(
                    icon: Icon(_isObscured ? Icons.visibility_outlined : Icons.visibility_off_outlined),
                    onPressed: () {
                      setState(() {
                        _isObscured = !_isObscured;
                      });
                    },
                  ),
                ),
              ),
              const SizedBox(height: 20),

              ClipRRect(
                borderRadius: BorderRadius.circular(10),
                child: LinearProgressIndicator(
                  value: _strength == 0 ? 0.05 : _strength,
                  backgroundColor: const Color(0xFF252526), 
                  color: _strengthColor,
                  minHeight: 15,
                ),
              ),
              const SizedBox(height: 15),

              Text(
                _strengthText,
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: _strengthColor,
                ),
              ),

              if (_suggestions.isNotEmpty) ...[
                const SizedBox(height: 30),
                const Text(
                  "✨ Better Suggestions (Click to apply):",
                  style: TextStyle(fontWeight: FontWeight.bold, color: Colors.grey),
                ),
                const SizedBox(height: 15),
                Wrap(
                  spacing: 8.0,
                  runSpacing: 8.0,
                  children: _suggestions.map((suggestion) {
                    return ActionChip(
                      label: Text(suggestion),
                      backgroundColor: const Color(0xFF252526), 
                      side: BorderSide(color: Colors.grey.shade700),
                      labelStyle: const TextStyle(color: Colors.white),
                      avatar: const Icon(Icons.bolt, color: Colors.amber, size: 18),
                      onPressed: () {
                        setState(() {
                          _password = suggestion;
                          _controller.text = suggestion;
                          _controller.selection = TextSelection.fromPosition(
                              TextPosition(offset: _controller.text.length));
                        });
                      },
                    );
                  }).toList(),
                ),
              ],

              const SizedBox(height: 30),
              const Divider(color: Colors.grey),
              const SizedBox(height: 10),

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
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        children: [
          Icon(
            met ? Icons.check_circle : Icons.radio_button_unchecked,
            color: met ? const Color(0xFF22C55E) : Colors.grey[600], 
            size: 20,
          ),
          const SizedBox(width: 12),
          Text(
            text,
            style: TextStyle(
              color: met ? Colors.white : Colors.grey[500], 
              decoration: met ? TextDecoration.none : TextDecoration.none,
            ),
          ),
        ],
      ),
    );
  }
}