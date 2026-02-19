// disney.js

// Make openVideoModal global so it can be called from onclick attributes
window.openVideoModal = function (card) {
    const videoModalElement = document.getElementById('videoModal');
    if (!videoModalElement) return;

    const videoModal = new bootstrap.Modal(videoModalElement);
    const modalHero = document.getElementById('modalHero');

    // Extraction des données
    const data = {
        id: card.getAttribute('data-id'),
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

    // Store current video ID globally for playModalVideo
    window._currentVideoId = data.id;

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
    modalHero.style.backgroundImage = `url('${data.thumbnail}')`;

    // Activer/désactiver le bouton Play selon la présence d'un trailer
    const playButton = document.getElementById('modalPlayButton');
    if (data.trailer && data.trailer !== 'null' && data.trailer !== '') {
        if (playButton) {
            playButton.disabled = false;
            playButton.style.opacity = '1';
            playButton.style.cursor = 'pointer';
            playButton.title = '';
        }
    } else {
        if (playButton) {
            playButton.disabled = true;
            playButton.style.opacity = '0.4';
            playButton.style.cursor = 'not-allowed';
            playButton.title = 'Aucun trailer disponible';
        }
    }

    videoModal.show();

    // Cleanup when modal is closed
    videoModalElement.addEventListener('hidden.bs.modal', function () {
        window._currentVideoId = null;
    }, { once: true }); // Use once: true to avoid stacking listeners
};

// Function to play the video from the modal button — navigates to the streaming page
window.playModalVideo = function () {
    if (window._currentVideoId) {
        window.location.href = '/watch/' + window._currentVideoId;
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
