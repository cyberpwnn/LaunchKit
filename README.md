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
| `/help ` | USE IT DAMMIT |
| `/launchkit env key value` | Sets a launchkit environment variable |
| `/launchkit set pack <url to config raw>` | Sets what configuration launchkit uses.|
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

### Specific Responses to LaunchKit

| Message | Meaning |
|---------|-------|
| `@ppm:state=Ready` | This is a state change. This is indended to show up as a minimal status indicator in an interface. |
| `@ppm:progress=0.264` | LaunchKit is 26.4% done with whatever the last State message was. |
| `@ppm:crashed=C:/Users/Someone/.../.../crashlog-123.txt` | The client under launchkit has crashed, Launchkit is simply providing the generated crash log. |
| `@ppm:validated` | This is when launchkit is validated. This is useful when running `/minecraft validate`. |
| `@ppm:running` | This is when launchkit has bound to the client process and the game is considered running. |
| `@ppm:stopped` | This is when the game has stopped. Either by crashing or by your command (`/minecraft stop`) |

### Configuration

Please check the wiki for configuration!
