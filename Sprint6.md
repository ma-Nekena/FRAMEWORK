##  Annotation
    [] creation de l'annotation @RestApi

## FrontControllerServlet
    [] Verification si la methode est annotee
        si oui
            - [] definir le header http de reponse (response.setContentType("application/json; charset=UTF-8"))
            - [] convertir les resultat de la methode en chaine de caractere json
            - [] ecriver directement dans la reponse avec response.getwriter().print()

        sinon,
            - [] consevoir le comportement habituel

