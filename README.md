# LaunchKit
Just launch it

# Usage
Using LaunchKit is pretty simple

## Launching LaunchKit

Launching comes with several options, however the simplest way is 
```
java -jar LaunchKit.jar
```

You can also configure any of these variables via launch parameters: https://github.com/cyberpwnn/LaunchKit/blob/master/src/main/java/org/cyberpwn/launchkit/Environment.java

```
java -jar LaunchKit.jar !log_level=1 !root_folder_name=YourProjectName !download_threads=16
```

## Command LaunchKit
Using the output stream to LaunchKit (assuming you started the process) you can send commands to launchkit.

| Command | Usage |
|---------|-------|
| `/launchkit stop` | Gracefully stops launchkit. Does not kill the client if it's running. |
| `/minecraft stop` | Crashes minecraft if it's running. Does not kill launchkit |
| `/minecraft validate` | Downloads any missing resources and properties to prepare for launch. |
| `/minecraft authenticate` | Authenticate with a previously stored access token. |
| `/minecraft authenticate <username> <password>` | Authenticate with the username and password to generate an access token for future use. |
| `/minecraft authenticate <profileName> <profileType> <uuid> <accessToken>` | Authenticate externally and sideload the information to launchkit. Using this method does not store the access token in LaunchKit, meaning you will have to continue using this command to continue launching. |
| `/minecraft start` | Launches the game. If the game is not validated yet, LaunchKit will validate it for you. |

## Responding to LaunchKit
All messages indended to be listened to are sent with the following format
```
@ppm:<message>
```

For example, `@ppm:hello world` means `hello world`
