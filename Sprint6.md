##  Annotation
    [OK] creation de l'annotation @RestApi

## FrontControllerServlet
    [OK] Verification si la methode est annotee
        si oui
            - [OK] definir le header http de reponse (response.setContentType("application/json; charset=UTF-8"))
            - [OK] convertir les resultat de la methode en chaine de caractere json
            - [OK] ecriver directement dans la reponse avec response.getwriter().print()

        sinon,
            - [OK] consevoir le comportement habituel

