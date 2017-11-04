# Linchuan Chen
# CSE 755 LISP Project

# Java compiler
JAVAC = javac

# Source directory
SRC = src/

# Bin directory
BIN = bin/

# Java compiler flags
JAVAFLAGS = -g

# Creating a .class file
COMPILE = $(JAVAC) $(JAVAFLAGS)

# One of these should be the "main" class listed in Runfile
		
CLASS_FILES = bin/errorhandling/LispException.class \
		bin/debug/Debug.class \
		bin/frontend/TokenType.class \
		bin/frontend/Sexp.class \
		bin/frontend/Parser.class \
		bin/backend/Interpreter.class \
		bin/ui/Ui.class \
		bin/ui/Main.class
		
# The first target is the one that is executed when you invoke
# "make". 

all: $(CLASS_FILES) 

# The line describing the action starts with <TAB>
$(BIN)%.class : $(SRC)%.java
	$(COMPILE) $< -sourcepath $(SRC) 
	
clean:
	rm $(CLASS_FILES)
