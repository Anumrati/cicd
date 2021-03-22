FROM DOCKER_BUILDER

ARG app_src=""
ENV MICROSERVICE=$app_src

RUN groupadd -r cicd && useradd --no-log-init -r -g cicd cicd

ADD $MICROSERVICE /cicd/
RUN mv /cicd/*.jar /cicd/app.jar
RUN chown -R cicd:cicd /cicd
RUN chmod 744 /cicd/app.jar
USER cicd
WORKDIR cicd
CMD java ${JAVA_OPTS} -jar app.jar