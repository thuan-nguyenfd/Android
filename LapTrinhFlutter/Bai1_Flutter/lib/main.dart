import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatelessWidget {
  const MyHomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: ElevatedButton(
          child: const Text('Show me'),
          onPressed: () {
            final now = DateTime.now();
            final formattedTime = DateFormat('HH:mm:ss').format(now);

            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text('Current Time: $formattedTime'),
              ),
            );
          },
        ),
      ),
    );
  }
}