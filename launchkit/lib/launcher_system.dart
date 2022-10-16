import 'package:launchkit/sugar.dart';

const String version = "2.0.0";

class LauncherSystem {
  final String path;

  LauncherSystem({required this.path});

  Future<void> execute(List<String> arguments) async {
    info("LaunchKit $version");
  }
}
