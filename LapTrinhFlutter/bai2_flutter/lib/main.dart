import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  void _showSnackbar(BuildContext context, String message){
    ScaffoldMessenger.of(context).showSnackBar(
    SnackBar(content: Text('ban da chon: $message'),),
    );
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Thuc don',
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Thuc don'),
        ),
        body: Builder(builder: (BuildContext context)=>ListView(
          children: <Widget>[
            ListTile(
              title: const Text('Chân gà quái thú'),
              onTap: (){
                _showSnackbar(context, 'Chân gà quái thú');
              },
            ),
            ListTile(
              title: const Text('Ếch núp lùm'),
              onTap: (){
                _showSnackbar(context, 'Ếch núp lùm');
              },
            ),
            ListTile(
              title: const Text('Đậu hũ lướt ván'),
              onTap: (){
                _showSnackbar(context, 'Đậu hũ lướt ván');
              },
            ),
          ],
        )),
      ),

    );
  }
}


