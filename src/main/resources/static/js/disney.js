// disney.js

// Make openVideoModal global so it can be called from onclick attributes
window.openVideoModal = function(card) {
    const videoModalElement = document.getElementById('videoModal');
    if (!videoModalElement) return;

    const videoModal = new bootstrap.Modal(videoModalElement);
    const videoPlayer = document.getElementById('modalVideoPlayer');
    const modalHero = document.getElementById('modalHero');

    // Extraction des données
    const data = {
        title: card.getAttribute('data-title'),
        description: card.getAttribute('data-description'),
        year: card.getAttribute('data-year'),
        duration: card.getAttribute('data-duration') + 'm',
        thumbnail: card.getAttribute('data-thumbnail'),
        rating: card.getAttribute('data-rating'),
        genre: card.getAttribute('data-genre'),
        trailer: card.getAttribute('data-trailer'),
        cast: card.getAttribute('data-cast') || 'Non disponible'
    };

    // Mise à jour des textes
    document.getElementById('modalTitle').textContent = data.title;
    document.getElementById('modalDescription').textContent = data.description;
    document.getElementById('modalYear').textContent = data.year;
    document.getElementById('modalDuration').textContent = data.duration;
    document.getElementById('modalGenre').textContent = data.genre;
    
    // Cast list might not exist in V2 modal yet, but let's try to set it if element exists
    const modalCastElement = document.getElementById('modalCast');
    if (modalCastElement) {
        modalCastElement.textContent = data.cast;
    }

    // Toujours afficher la miniature en arrière-plan
    // Using simple url() because linear-gradient might be handled by CSS overlay
    modalHero.style.backgroundImage = `url('${data.thumbnail}')`;
    
    // Assurer que le lecteur vidéo est masqué et en pause par défaut
    videoPlayer.pause();
    videoPlayer.style.display = 'none';
    videoPlayer.src = ""; // Effacer la source pour éviter tout chargement inattendu

    // Si un trailer existe, préparer sa source
    if (data.trailer && data.trailer !== 'null' && data.trailer !== '') {
        videoPlayer.src = data.trailer;
    }

    videoModal.show();
    
    // Cleanup when modal is closed
    videoModalElement.addEventListener('hidden.bs.modal', function () {
        videoPlayer.pause();
        videoPlayer.src = "";
    }, { once: true }); // Use once: true to avoid stacking listeners
};

// Function to play the video from the modal button
window.playModalVideo = function() {
    const videoPlayer = document.getElementById('modalVideoPlayer');
    const modalHero = document.getElementById('modalHero');
    
    if (videoPlayer.src && videoPlayer.src !== window.location.href) {
        videoPlayer.style.display = 'block';
        videoPlayer.play().catch(e => console.log("Error playing video:", e));
        modalHero.style.backgroundImage = 'none';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    // 1. Navbar Scroll Effect
    window.onscroll = function () {
        const navbar = document.querySelector('.navbar');
        if (navbar) {
            if (window.scrollY > 50) {
                navbar.style.backgroundColor = 'var(--disney-nav)';
            } else {
                navbar.style.backgroundColor = 'transparent';
            }
        }
    };

    // 2. Brand Card Video Hover Effect
    const brandCards = document.querySelectorAll('.brand-card');
    brandCards.forEach(card => {
        const video = card.querySelector('video');
        if (video) {
            card.addEventListener('mouseenter', () => {
                video.play();
            });
            card.addEventListener('mouseleave', () => {
                video.pause();
                video.currentTime = 0;
            });
        }
    });
});
