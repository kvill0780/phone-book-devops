## Description
<!-- Décrivez les changements apportés -->

## Type de changement
- [ ] Bug fix
- [ ] Nouvelle fonctionnalité
- [ ] Breaking change
- [ ] Documentation

## Checklist
- [ ] Tests ajoutés/mis à jour
- [ ] Documentation mise à jour
- [ ] Pipeline CI/CD passe
- [ ] Code review effectué

## Tests
<!-- Comment avez-vous testé vos changements ? -->

```bash
# Docker Compose
docker-compose up -d
docker-compose ps

# Kubernetes
cd k8s && ./deploy.sh
kubectl get pods -n phone-book
```
