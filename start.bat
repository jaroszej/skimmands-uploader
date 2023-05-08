@echo off
java -Xmx1g -DsyncInterval=15 -DsqlitePath="config/phantombot.db" -DmongoConnStr="mongodb+srv://<username>:<password>@<hostname>.net/&authMechanism=SCRAM-SHA-256&retryWrites=true&w=majority" -jar skimmands-uploader.jar > skimmands-uploader-log.txt

echo.^
 & echo [ATTENTION] You may close this window. Log output is available at skimmands-uploader-log.txt
echo.^

pause
