net stop "beasvc zmeg_AdminServer"
xcopy /s /e /y D:\update_temp E:\work\idea\zmeg\zmeg\web
rd /s /q D:\update_temp
net start "beasvc zmeg_AdminServer"