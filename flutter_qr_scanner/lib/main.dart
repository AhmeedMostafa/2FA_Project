import 'package:flutter/material.dart';
import 'package:mobile_scanner/mobile_scanner.dart';

void main() {
  runApp(const MaterialApp(home: QrScannerApp()));
}

class QrScannerApp extends StatefulWidget {
  const QrScannerApp({super.key});

  @override
  State<QrScannerApp> createState() => _QrScannerAppState();
}

class _QrScannerAppState extends State<QrScannerApp> {
  String scannedCode = "Scan a QR Code";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('QR Scanner'), backgroundColor: Colors.teal),
      body: Column(
        children: [
          Expanded(
            flex: 5,
            child: MobileScanner(
              onDetect: (capture) {
                final List<Barcode> barcodes = capture.barcodes;
                for (final barcode in barcodes) {
                  setState(() {
                    scannedCode = barcode.rawValue ?? "No Data";
                  });
                }
              },
            ),
          ),
          Expanded(
            flex: 1,
            child: Center(
              child: Text(
                scannedCode,
                style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
            ),
          ),
        ],
      ),
    );
  }
}