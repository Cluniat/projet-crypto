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

