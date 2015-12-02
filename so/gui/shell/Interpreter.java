package so.gui.shell;

import so.filesystem.main.FileSystemController;

public class Interpreter {

	public void readCommand(String curCommand, FileSystemController fs) throws WrongCommandException, ShellAnswerException{
		
		String[] in = this.divideCurCommand(curCommand);
		String cmd = in[0];
		String param = in[1];
		
		if(cmd.equals("echo")){
			throw new ShellAnswerException(param);
		}else if(cmd.equals("clear")){
			throw new ShellAnswerException("\n\n\n\n\n\n\n\n\n\n\n");
		}else if(cmd.equals("sayshi")){
			throw new ShellAnswerException("Hola "+param);
		}else if(cmd.equals("-1")&&param.equals("-1")){
			throw new ShellAnswerException("Wrong Params");
		}else if(cmd.equals("initFileSystem")){
			fs.init();
		}else if(cmd.equals("formatCache")){
			fs.formatCache();
		}else{
			throw new WrongCommandException("Wrong Command:" + cmd);
		}
	}
	
	public String[] divideCurCommand(String curCommand) {

		String[] parts = curCommand.split(" ");
		
		if(parts.length==2){
			return parts;
		}else if(parts.length==1){
			String[] cmd = {parts[0],"-1"};
			return cmd;
		}else if (parts.length > 2) {
			String[] error = {"-1","-1"};
			return error;
		} else {
			String[] error = {"-1","-1"};
			return error;
		}

	}
}
