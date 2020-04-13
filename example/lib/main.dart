import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:googlesigninplugin/googlesigninplugin.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await Googlesigninplugin.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    print("Running");

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Text('Running on: $_platformVersion\n'),
              FlatButton(
                onPressed: () async {
                  print('Test');
                  try {
                    String clientToken = await Googlesigninplugin.signIn(clientIDAndroid: "1074291073899-rm0381fcsn9fbss5np1dvtvp8c0sqj4k.apps.googleusercontent.com", clientIDiOS: "1074291073899-gb9qk5rm8me3bp3b1subvct5lnadjr95.apps.googleusercontent.com");
                    print("Worked out!");
                    print(clientToken);
                  } catch (error) {
                    print(error.toString());
                  }

                },
                child: Text("Login"),
              )
            ],
          ),
        ),
      ),
    );
  }
}
