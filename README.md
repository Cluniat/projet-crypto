# projet-crypto

## Questions

### Protocole Multiplication
#### Q1
Voir code (classe Multiplication)

#### Q2
Par la propriété homomorphique de Paillier, on retrouve facilement que a = eα + δ de par sa construction.  
De plus on a :  
a' = Paillier.Decrypt(([β]^a [π]^−1 [γ]^−e mod n2 ))  
=> a' = aβ - π - eγ    Par les propriétés homomorphique de Paillier  
=> a' = (eα + δ)β - δβ - eαβ  
=> a' = eαβ + δβ - δβ - eαβ  
=> a' = 0

#### Q3
Après le protocole, les seules informations décryptées qu'a obtenu Bob sont a et a'.  
Or nous avons montré à la question précédente que a' = 0 si Alice respecte le protocole, donc il ne peut pas en extraire
 d'information. Nous avons également montré que a = eα + δ. Or δ est choisi aléatoirement par Alice et la seule 
 information que possède Bob est son encryption avec Pailler. Comme Paillier est sémantiquement sûr, il ne peut donc 
 obtenir aucune information sur δ, et donc de fait aucune information sur α. Pour β et γ, il n'obtient que 
 leurs valeurs encryptées par Paillier et ne peut donc pas en tirer d'information.

#### Q4
En effectuant les vérifications 4.a et 4.b, Bob encrypte a et a' en utilisant le r donné par la fonction de décryption au 
lieu d'en regénérer un aléatoirement. Donc il doit normalement obtenir la même encryption qu'Alice. Comme il compare
cette encryption avec le résultat du calcul qu'est sensé avoir effectué Alice avant la décryption, si a et a' encryptent les bonnes valeurs, il est sensé obtenir le même résultat.

#### Q5
Supposons que γ != αβ et que les vérifications 4.a et 4.b n'échouent pas :
on a donc 
a' = aβ - π - eγ  
=> a' = (eα + δ)β -  π - eγ   
Dans le cas ou π = δβ, on a donc   
a' = (eα + δ)β - δβ - eγ  
=> a' = eαβ + δβ - δβ - eγ  
=> a' = eαβ - eγ  
donc a' = 0 si et seulement si γ = αβ. Or ce n'est pas le cas, donc a' != 0

Dans le cas où π != δβ, on a:  
a' = (eα + δ)β -  π - eγ  
supposons que a' = 0:  
0 = (eα + δ)β -  π - eγ  
=> e(αβ - γ) = π - δβ  
Or αβ != γ donc αβ - γ != 0 et π != δβ donc π - δβ != 0. Posons c = αβ - γ et d = π - δβ, on a donc  
ec = d <=> a' = 0
e est choisi aléatoirement dans Z/nZ indépendament de c et d, donc la probabilité d'avoir ec = d est très faible


#### Q6
D'après la question précédente, si γ est différent de αβ et que les vérifications 4.a et 4.b sont valides, alors a' est différent de 0 avec une probabilité proche de 1.  
Or si a' est différent de 0, le protocole échoue.  
Donc si γ est différent de αβ, le protocole échoue avec une probabilité proche de 1.

#### Q7
On peut facilement utiliser le protocole MultiProof dans le protocole Mutliplication pour sécuriser l'étape 2.  
En effet cela permet à Bob d'ếtre sûr que Alice lui envoie bien une encryption de (x + r)(y + s).  
Pour cela, il suffit d'appliquer le protocole MultiProof en prenant α = (x + r) et β = (y + s)

### Exercice 105

*Bob (**B**) a des valeurs booléennes, Alice (**A**) une fonction qui prend des valeurs booléennes et donne un résultat (booléen).  
Les clauses sont sous la forme : x1 ^ x2 ^ x3 v ¬x1 ^ x5 ^ ¬x4 v ...*

L'objectif du protocole est de permettre à Bob d'obtenir le résultat de la DNF avec les valeurs booléenes qu'il possède.  

Dans le cas idéal, Bob obtient le résultat final sans avoir la moindre information sur la DNF et Alice n'a obtenu aucune
information sur les valeurs de Bob.  
Pour arriver à se rapprocher autant que possible du cas idéal, nous proposons le protocole ci-dessous :

- **B** génère un couple de clés privé/public pour Paillier et transmet la clé publique à **A**
- **B** chiffre les valeurs et les envoie à **A**
- **A** calcule chaque clause disjonctive séparément (les suites de . ^ . ^ .) avec la méthode suivante :
- - Si il y a un ¬X alors Alice le remplace par **Paillier.Encrypt(1) ⊝ X**. On obtient de cette manière une négation de X.
- - **A** calcule la somme des Xi et soustrait le nombre total de Xi (ex : X1 ⊕ X2 ⊕ X3 ⊝ 3 = X). La clause est donc vraie si et seulement si X = 0.
En effet, la clause étant composée seulement de ^, X est vrai si X1, X2 et X3 sont vrai (X1 + X2 + X3 = 3).
- Pour que **B** ne puisse pas obtenir d'information sur le résultat précis des clauses, **A** les multiplie par un nombre aléatoire pour brouiller les résultats faux.
**B** pourra toujours connaitre les clauses vérifiées mais n'aura aucune information sur les clauses fausses.
- Pour ne pas donner d'information sur le nombre de clauses, **A** renvoie les clauses calculées en les permuttant aléatoirement (et en ajoutant des clauses fausses (donc ≠ 0)
- **B** décrypte chaque clause puis les multiplie entre elles, si le résultat final est **0** alors F(xi,..) = True

#### Limite du protocole

Par rapport au cas idéal, ce protocole présente certains défauts, mais il présente plusieurs très bon points :

  
Pour la sécurité des informations de Bob, ce protocole correspond parfaitement au cas idéal : En effet Alice n'obtient
que des encryptions des valeurs de Bob, donc elle n'a aucun moyen d'en déduire les valeurs envoyées par Bob car le cryptosystème
de Paillier est sémantiquement sûr.  
Par contre avec ce protocole, Bob arrive à obtenir plus d'information sur la DNF que ce qu'il en obtient dans le cas idéal.
En effet il peut pour chaque calcul de la DNF (exécution du protocole) obtenir le nombre exacte de clauses vraies.
Comme il ne connait pas le nombre total de clause ni lesquelles sont vraies, il lui est probablement impossible d'obtenir 
des informations précises sur la DNF. Il peut donc seulement effectuer des suppositions sur le contenu de certaines clauses. 
Mais il ne pourra pas avoir plus de certitudes que dans le cas idéal.

Avec ce protocole, Alice peut se sentir lésée car Bob obtient plus d'information que dans le cas idéal, mais Bob devrait être satisfait par ce protocole.


### Mariage cryptographique

*Bob (**B**) a un bracelet électronique qui connaît sa position (xB, yB) à tout moment. Alice (**A**) doit pouvoir savoir à tout moment si **B** est à moins de 100 mètres, sans connaître la position de **B**.*

Pour répondre à ce problème nous proposons deux protocoles, qui ont chacun leurs limites :

#### Protocole 1

Voir implémentation classe CryptoMariage1

On suppose que :
- Le bracelet est fiable et que **B** ne peut pas le pirater (protocole codé en dur, système de sécurité sur le bracelet empêchant Bob de l'ouvrir sans provoquer d'alarme, Bob ne peut pas envoyer de message à la place du bracelet, etc..).
- Le bracelet dispose d'une faible puissance de calcul.

Protocole :
- **A** envoie à **B** (à son bracelet) sa position (xA, yA) de manière sécurisée (encryption que le bracelet pourra décrypter)
- **B** calcule la différence en *x* et la différence en *y*
- Si *x > 100* ou *y > 100*, **B** renvoie Faux (on est sûr que la distance est supérieure à √(100²+100²) = 141 mètres)
- Sinon **B** calcule *x² + y²* et teste si le résultat est inférieur à *10000* (100²)
- Si *x² + y² < 10 000*, alors **B** renvoie sa position, sinon **B** renvoie Faux

Limites : On suppose une puissance de calcul du bracelet de **B**, c'est notamment pour ça qu'on ne calcule pas les carrés si ce n'est pas utile. De plus Alice doit envoyer sa position au bracelet, il faut donc que celui-ci soit vraiment fiable.

#### Protocole 2

On suppose de même que le bracelet est fiable, que **B** ne peut pas le pirater et qu'il dispose d'une faible puissance de calcul.

Protocole :
- **A** génère une paire de clés (pk, sk) <- Paillier.KeyGen().
- **A** envoie à **B** les encryptions : 
  - *XA <- Paillier.Encrypt(xA, pk)* 
  - *YA <- Paillier.Encrypt(yA, pk)*
  - *XA² <- Paillier.Encrypt(xA², pk)* 
  - *YA² <- Paillier.Encrypt(yA², pk)* 
- **B** calcule et retourne sa distance² par rapport à **A** : 
  - *D² = Paillier.Encrypt((xA - xB)² + (yA - yB)²)* 
  - *D² = Paillier.Encrypt(xA² + yA² + xB² + yB² - 2.xA.xB - 2.yA.yB)* 
  - *D² = Paillier.Encrypt(xA²) + Paillier.Encrypt(yA²) + Paillier.Encrypt(xB² + yB²) + Paillier.Encrypt(-2.xA.xB) + Paillier.Encrypt(-2.yA.yB)*
  - *D² = XA² + YA² + Paillier.Encrypt( xB² + yB²) + (-2 xB).XA + (-2 yB).YA*
- **A** décrypte *D²* et vérifie si celle-ci est inférieure à 10 000 (100²)
  - Si oui, **A** envoie à **B** le `r` utilisé et la valeur de la distance
  - **B** vérifie que l'encryption correspond et renvoie ses coordonnées

Limites : 
- On suppose une puissance de calcul du bracelet de **B**. Ici, le bracelet n'a pas besoin de calculer les encryptions et carré des coordonnées de A, ce qui réduit déjà la puissance nécessaire.
- **A** connaît sa distance avec **B**, ce qui donne une information sur la position de **B**, ce que l'on peut vouloir éviter, si celui-ci n'est pas à moins de 100 mètres de **A**.
