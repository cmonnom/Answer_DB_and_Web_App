This directory has 1 container:
- downloads a custom image of a shinyapp container from dockerhub with all R packages needed for MuSiCa
- docker-compose should mount: 
	./musica which is copied to /srv/shiny-server/musica
	the shiny server config file
	path to /opt/answer/files/vcfs and /opt/answer/links/vcfs
	path to /PHG_Clinical/cases
- the image contains the same groups used by answer-test
- user "shiny" in the container has uid 999.
- in docker-compose,  user: 999:58647 runs the container as the "shiny" user in the "answer" group
	