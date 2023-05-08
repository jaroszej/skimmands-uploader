@echo off
java -Xmx1g -DsyncInterval=15 -DsqlitePath="config/phantombot.db" -DmongoConnStr="mongodb+srv://liddleMan:N7Qko4rKQ4WmXEtD@skimmands.jvvlslp.mongodb.net/&authMechanism=SCRAM-SHA-256&retryWrites=true&w=majority" -jar skimmands-uploader.jar

echo.^
 & echo [ATTENTION] You may close this window. Log output is available at skimmands-[date].log files
echo.^

pause
