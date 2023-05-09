@echo off
cmd /k java -Xmx1g -DsyncInterval=5 -DsqlitePath="config/phantombot.db" -DmongoConnStr="mongodb+srv://liddleMan:N7Qko4rKQ4WmXEtD@skimmands.jvvlslp.mongodb.net/&authMechanism=SCRAM-SHA-256&retryWrites=true&w=majority" -jar skimmands-uploader.jar

echo.^
 & echo [ATTENTION] You may close this window...
echo.^
 & echo [ATTENTION] Check skimmands_uploader_logs directory for details...
echo.^

pause
