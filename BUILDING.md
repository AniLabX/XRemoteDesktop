
`XRemote Desktop` is plain `Java` app that uses `JavaFX` for UI. It requires `JDK 17+` for building and running or `GraalVM JDK 17` if you plan build `Native Image`

If you want to build or develop app, clone this repo, make sure to rename `gradle.properties.example` into `gradle.properties`, fill `GraalVM` paths on it and open project in desired IDE or terminal

## JAR file

Execute ```gradlew shadowJar``` task. This task will produce `fat` jar that can be launched with any `JRE/JDK 17+` with no additional dependencies

*Note: app is designed to work as `Native Application` without `JRE/JDK`*

## GraalVM Native Image

### Prerequisites

Install latest [GraalVM JDK 17](https://www.graalvm.org/downloads/) for your system. If you plan building image for `MacOS`, use [GraalVM CE Gluon 22.1.0.1-Final](https://github.com/gluonhq/graal/releases/tag/gluon-22.1.0.1-Final) instead. App will not build with latest `GraalVM` on `MacOS`

#### Windows

Follow [Windows instructions](https://www.graalvm.org/latest/docs/getting-started/windows/)

#### Linux

Follow [Linux instructions](https://www.graalvm.org/latest/docs/getting-started/linux/)

#### MacOS

Follow [MacOS instructions](https://www.graalvm.org/latest/docs/getting-started/macos/)

### Building

#### Windows

Execute ```gradlew nativeBuild``` task from terminal. After success, binary file will be in `\build\gluonfx\x86_64-windows` folder

#### Linux

Rename `buildUnixDarwin.sh.example` into `buildUnixDarwin.sh` and fill required fields. Open `Terminal` and execute:  
```cd project_dir```  
```chmod +x buildUnixDarwin.sh```  
```./buildUnixDarwin.sh```  
After success, binary file will be in `/build/gluonfx/x86_64-linux` folder

#### Linux

Rename `buildUnixDarwin.sh.example` into `buildUnixDarwin.sh` and fill required fields. Open `Terminal` and execute:  
```cd project_dir```  
```sudo xattr -d com.apple.quarantine buildUnixDarwin.sh```  
```chmod +x buildUnixDarwin.sh```  
```./buildUnixDarwin.sh```  
After success, binary file will be in `/build/gluonfx/x86_64-macos` folder

## Packing (UPX'ing) binary

You can produce smaller binary with `UPX`

#### Windows

**DON`T DO THAT. APP WILL NOT WORK**

#### Linux

Install latest version of `upx` and execute this command to pack binary: ```upx -9 -o {output_file_path} {input_file_path}```

Produced binary will be ~2-3 times smaller than original one

#### MacOS

**DON`T DO THAT. APP WILL NOT WORK**
