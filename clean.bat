@ECHO OFF
FOR /D /R %%i IN (out build log) DO (
    IF EXIST %%i (
        RD /S /Q %%i
        @ECHO deleted %%i
    )
)
@echo finished
