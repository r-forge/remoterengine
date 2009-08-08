REMOTERENGINE=$(R_HOME)/bin/remoterengine
REMOTERENGINETEST=$(R_HOME)/bin/remoterengine_testclient
FAKE_OBJ=remoterengine.so

all: 
	ant all
	@touch $(FAKE_OBJ)
	install "remoterengine" $(REMOTERENGINE)
	install "remoterengine_testclient" $(REMOTERENGINETEST)

clean:
	rm -fv $(FAKE_OBJ)



