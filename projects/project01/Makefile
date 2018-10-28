

TARBALL_DIR = cis457_project01
tarball:
	mkdir -p $(TARBALL_DIR)
	rsync -Chavz *.java ftp_* $(TARBALL_DIR)
	chmod   a+rx $(TARBALL_DIR)
	chmod -R a+r $(TARBALL_DIR)/*
	#chmod -R a+x $(TARBALL_DIR)/*.{gnuplot,sh}
	tar -cvzf $(TARBALL_DIR).tar.gz $(TARBALL_DIR)
	chmod a+r $(TARBALL_DIR).tar.gz


