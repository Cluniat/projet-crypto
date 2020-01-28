# projet-crypto

## Questions

### Protocole Multiplication
#### Q1
Voir code (classe Multiplication)

#### Q2
Par la propriété homomorphique de Paillier, on trouve retrouve facilement que a = eα + δ de par sa construction.  
De plus on a :  
a' = Paillier.Decrypt(([β]^a [π]^−1 [γ]^−e mod n2 ))  
=> a' = aβ - π - eγ    Par les propriétés homomorphique de Paillier  
=> a' = (eα + δ)β - δβ - eαβ  
=> a' = eαβ + δβ - δβ - eαβ  
=> a' = 0

#### Q3
Après le protocole, les seules informations décrypté qu'a obtenu Bob sont a et a'.  
Or nous avons montré à la question précédente que a' = 0 si Alice respecte le protocole, donc il ne peut pas en extraire
 d'infomation. Nous avons également montré que a = eα + δ. Or δ est choisi aléatoirement par Alice et la seule 
 information que possède Bob est son encryption avec Pailler. Comme Paillier est sémantiquement sûr, il ne peut donc 
 obtenir aucune information sur δ, et donc de fait aucune information non plus sur α. Pour β et γ, il n'obtient que 
 leurs valeurs encrypté par Paillier et ne peut donc pas en tirer d'information.

#### Q4
En effectuant les vérification 4.a et 4.b, Bob encrypte a et a' en utilisant le r donné par la fonction de décryption au 
lieu d'en regénérer un aléatoirement. Donc il doit normalement obtenir la même encryption que qu'Alice. Comme il compare
cette encryption avec le résultat du calcul qu'est sensé avoir effectué Alice avant la décryption, Si a et a' encrypte les bonnes valeurs,
il est sensé obtenir le même résultat.

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
D'après la question précédente, si γ est différent de αβ et que les vérifications 4.a et 4.b sont valide, alors a' est différent de 0 avec une probabilité proche de 1.  
Or si a' est différent de 0, le protocole échoue.  
Donc si γ est différent de αβ, le protocole échoue avec une probabilité proche de 1.

#### Q7
On peut facilement utiliser le protocole MultiProof dans le protocole Mutliplication pour sécuriser l'étape 2.  
En effet cela permet à Bob d'ếtre sur que Alice lui envoie bien une encryption de (x + r)(y + s).  
Pour cela, il suffit d'appliquer le protocole MultiProof en prenant α = (x + r) et β = (y + s)

### Exercice 105

*Bob (**B**) a des valeurs booléennes, Alice (**A**) une fonction qui prend des valeurs booléennes et donne un résultat (booléen).  
Les clauses sont sous la forme : x1 ^ x2 ^ x3 v ¬x1 ^ x5 ^ ¬x4 v ...*

Protocole :
- **B** chiffre les valeurs et envoie à **A**
- **A** calcule chaque clause disjonctive séparément (les suites de . ^ . ^ .)
- - Si ¬X alors, remplacer par **Paillier.Encrypt(1) ⊝ X**
- - **A** calcule la somme des Xi et soustrait le nombre total de Xi (ex : X1 ⊕ X2 ⊕ X3 ⊝ 3 = X)
- - X = 0 si c'est vrai, **A** les multiplie donc par un nombre aléatoire pour brouiller les résultats faux
- **A** renvoie les clauses calculées en les permuttant aléatoirement (et en ajoutant des clauses fausses donc ≠ 0)
- **B** décrypte chaque clause puis les multiplie entre elles, si le résultat final est **0** alors F(xi,..) = True

#### Pour aller plus loin

- Essayer de voir comment **B** peut récupérer la fonction de **A**
- Est-ce que **A** peut récupérer les valeurs de **B** ?

### Mariage cryptographique

*Bob (**B**) a un bracelet électronique qui connaît sa position (xB, yB) à tout moment. Alice (**A**) doit pouvoir savoir à tout moment si **B** est à moins de 100 mètres, sans connaître la position de **B**.*

Pour l'instant, on a 2 protocoles (à voir lequel est le mieux)

#### Protocole 1

On suppose que le bracelet est fiable (**B** ne l'a pas piraté et **A** peut lui envoyer des valeurs sans que qui que ce soit ne puisse récupérer ces valeurs) et qu'il dispose d'une faible puissance de calcul.

Protocole :
- **A** envoie à **B** (à son bracelet) sa position (xA, yA) non encryptée
- **B** calcule la différence en *x* et la différence en *y*
- Si *x > 100* ou *y > 100*, **B** renvoie Faux (on est sûr que la distance est supérieure à 141 mètres)
- Sinon **B** calcule *x² + y²* et teste si le résultat est inférieur à *10000* (100²)
- Si *x² + y² < 10 000*, alors **B** renvoie sa position, sinon **B** renvoie Faux

Principal problème : la puissance de calcul du bracelet de **B**, c'est notamment pour ça qu'on ne calcule pas les carrés si ce n'est pas utile.

#### Protocole 2

Protocole :
- **A** envoie à **B** les encryptions : XA, YA, XA² (= Paillier.Encrypt(xA²) ), YA²
- **B** calcule et renvoie *D = XA² + YA² + Paillier.Encrypt( xB² + yB² ) + XA ⊗ (-2 xB) + YA ⊗ (-2 yB)* (sa distance par rapport à **A**)
- **A** décrypte *D* et vérifie si celle-ci est inférieure à 10 000 (100²)

Problème : **A** connaît sa distance avec **B**
