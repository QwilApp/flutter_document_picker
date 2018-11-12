import 'package:flutter/material.dart';
import 'package:flutter_document_picker/flutter_document_picker.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  initState() {
    super.initState();
  }

  initPlatformState() async {
    final FileInfo info = await FlutterDocumentPicker.show();
    if (!mounted) return;
    setState(() {
      _platformVersion = info?.toString();
    });
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: new Text('Plugin example app'),
        ),
        body: new Center(
          child: new Text('Running on: $_platformVersion\n'),
        ),
        floatingActionButton: new FloatingActionButton(
          child: new Icon(Icons.http),
          onPressed: () {
            initPlatformState();
          },
        ),
      ),
    );
  }
}
