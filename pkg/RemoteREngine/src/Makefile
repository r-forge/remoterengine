REMOTERENGINE=$(R_HOME)/bin/remoterengine
REMOTERENGINETEST=$(R_HOME)/bin/remoterengine_testclient
REMOTERENGINEJUNIT=$(R_HOME)/bin/remoterengine_junit
FAKE_OBJ=remoterengine.so

all: 
	ant all
	@touch $(FAKE_OBJ)
	install "remoterengine" $(REMOTERENGINE)
	install "remoterengine_testclient" $(REMOTERENGINETEST)
	install "remoterengine_junit" $(REMOTERENGINEJUNIT)

clean:
	rm -fv $(FAKE_OBJ)



