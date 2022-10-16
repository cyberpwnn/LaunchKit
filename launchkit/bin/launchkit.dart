import 'package:launchkit/launcher_system.dart';
import 'package:launchkit/util/filesystem.dart';

Future<void> main(List<String> arguments) async {
  await LauncherSystem(path: AppData.findOrCreate("LaunchKit").path)
      .execute(arguments);
}
