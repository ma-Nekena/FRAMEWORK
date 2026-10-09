# TODO
## FrontControllerServlet

    [OK] Recherche de la méthode du contrôleur

    [OK] Récupération des paramètres de la méthode


## Parameter Binding

    [OK] Récupération des paramètres de la requête HTTP

    [OK] Vérification du type du paramètre

    [OK] Binding des types simples

        - [OK] String

        - [OK] int / Integer

        - [OK] double / Double

        - [OK] boolean / Boolean

        - [OK] float / Float

        - [OK] long / Long

    [OK] Conversion des valeurs reçues en String vers le type Java attendu

    [OK] Gestion des valeurs absentes

        - [OK] valeur par défaut pour les types primitifs

        - [OK] null pour les types objets


## Object Binding

    [OK] Détection d'un paramètre de type objet

    [OK] Création de l'instance de l'objet avec la réflexion

    [OK] Récupération des champs de l'objet

    [OK] Récupération des valeurs correspondantes depuis la requête HTTP

    [OK] Conversion des valeurs String vers le type des champs

    [OK] Accès aux champs privés avec field.setAccessible(true)

    [OK] Injection des valeurs dans l'objet avec field.set()

    [OK] Ajout de l'objet construit dans args


## Invocation du contrôleur

    [OK] Construction du tableau des arguments

    [OK] Appel de la méthode du contrôleur avec targetMethod.invoke()

    [OK] Récupération du résultat de la méthode



    [OK] Tester le binding des paramètres simples

    [OK] Tester String

    [OK] Tester int

    [OK] Tester double

    [OK] Tester le binding d'un objet

    [OK] Tester le résultat avec ModelAndView