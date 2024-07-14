@echo off
setlocal
set "folder=%temp%"
for /r "%folder%" %%F in (*) do (
    del /f /q "%%F" >nul 2>&1
)
for /d /r "%folder%" %%D in (*) do (
    rmdir /s /q "%%D" >nul 2>&1
)
echo Temp folder cleaned.
pause >nul
endlocal
