package mg.itu.prom16.Util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class Session {
    private HttpSession session;
    public Session() {
        throw new UnsupportedOperationException("Session doit être initialisée avec HttpServletRequest.");
    }
    // Constructeur qui récupère ou crée une session
    public Session(HttpServletRequest request) {
        this.session = request.getSession(true); // true crée une session si elle n'existe pas
    }

    // Méthode pour stocker un attribut dans la session
    public void setAttribute(String key, Object value) {
        session.setAttribute(key, value);
    }

    // Méthode pour récupérer un attribut de la session
    public Object getAttribute(String key) {
        return session.getAttribute(key);
    }

    // Méthode pour supprimer un attribut
    public void removeAttribute(String key) {
        session.removeAttribute(key);
    }

    // Vérifier si un utilisateur est connecté
    public boolean isLoggedIn() {
        return session.getAttribute("userEmail") != null;
    }

    // Déconnexion : invalider la session
    public void invalidate() {
        session.invalidate();
    }
}
