import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:mime/mime.dart';

class FlutterDocumentPicker {
  static const MethodChannel _channel = const MethodChannel('flutter_document_picker');

  static Future<FileInfo> show({FileType type}) async {
    type ??= FileType.allFiles;
    final Map file = await _channel.invokeMethod('show', <String, String>{'fileType': type.toString()});
    return file == null ? null : new FileInfo.fromJson(file.cast<String, dynamic>(), type: type);
  }
}

class FileInfo {
  FileInfo({this.fileName, this.uri, this.fileType, this.fileSize});

  final String fileName;
  final String uri;
  final String fileType;
  final int fileSize;

  factory FileInfo.fromJson(Map<String, dynamic> json, {FileType type}) {
    final dynamic size = json['fileSize'];
    final String path = json['path'];
    final String fileName = json['fileName'];
    return new FileInfo(
      fileName: fileName,
      uri: Platform.isIOS ? Uri.parse(path).toFilePath() : path,
      fileType: Platform.isAndroid ? json['type'] : lookupMimeType(fileName),
      fileSize: size is int ? size : int.parse(size),
    );
  }

  @override
  String toString() {
    return 'FileInfo{fileName: $fileName, uri: $uri, fileType: $fileType, fileSize: $fileSize}';
  }
}

class FileType {
  final String platform = Platform.operatingSystem;
  final int _index;

  FileType._(this._index);

  static FileType get allFiles => new FileType._(0);

  static FileType get plainText => new FileType._(1);

  static FileType get pdf => new FileType._(2);

  static FileType get images => new FileType._(3);

  static FileType get audio => new FileType._(4);

  static FileType get video => new FileType._(5);

  static FileType get archive => new FileType._(6);


  static Map<String, List<String>> get values =>
      <String, List<String>>{
        'android': const <String>[
          '*/*',
          'text/plain',
          'application/pdf',
          'image/*',
          'audio/*',
          'video/*',
          'application/archive'
        ],
        'ios': const <String>[
          'public.content',
          'public.plain-text',
          'com.adobe.pdf',
          'public.image',
          'public.audio',
          'public.video',
          'public.archive',
        ]
      };

  @override
  String toString() => values[platform][_index];
}
