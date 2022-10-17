import 'package:colorize/colorize.dart';
import 'package:launchkit/sugar.dart';
import 'package:microshaft/microshaft.dart';

const String version = "2.0.0";

class LauncherSystem {
  final String path;

  LauncherSystem({required this.path});

  Future<void> execute(List<String> arguments) async {
    info("LaunchKit $version");
    await MicroshaftClient(storage: FileStorage.load("$path/tokens.dat"))
        .authenticate((url, code) {
      print(Colorize("Please Visit ").white().toString() +
          Colorize(url).lightBlue().toString() +
          Colorize(" and enter the code ").white().toString() +
          Colorize(code).bgCyan().underline().toString());
    }).then((value) {
      warn("Hello ${value.profileName}, ${value.uuid}!");
    });
  }
}
