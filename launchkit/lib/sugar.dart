import 'package:colorize/colorize.dart';

void info(dynamic f, {String category = "LaunchKit"}) =>
    print(Colorize("[$category]: ").white().toString() +
        Colorize(f.toString()).lightGray().toString());

void infoSignificant(dynamic f, {String category = "LaunchKit"}) =>
    print(Colorize("[$category]: ").white().toString() +
        Colorize(f.toString()).white().toString());

void warn(dynamic f, {String category = "LaunchKit"}) =>
    print(Colorize("[$category]: ").white().toString() +
        Colorize(f.toString()).yellow().toString());

void error(dynamic f, {String category = "LaunchKit"}) =>
    print(Colorize("[$category]: ").white().toString() +
        Colorize(f.toString()).red().toString());

void network(dynamic f, {String category = "LaunchKit"}) =>
    print(Colorize("[$category]: ").white().toString() +
        Colorize(f.toString()).lightBlue().toString());

void attention(dynamic f, {String category = "LaunchKit"}) =>
    print(Colorize("[$category]: ").white().toString() +
        Colorize(f.toString()).bgLightMagenta().toString());
