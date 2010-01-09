on error resume next
Set fileSys = CreateObject("Scripting.FileSystemObject")
Set file = fileSys.GetFile(wscript.arguments(0))
wscript.echo file.ShortPath
