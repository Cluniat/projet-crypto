# projet-crypto

## Questions

### Protocole Multiplication

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
