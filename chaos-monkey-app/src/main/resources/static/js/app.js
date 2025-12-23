/**
 * ChaosFlix - Main Application Script
 */

// 1. Navbar Scroll Effect
window.onscroll = function () {
    const navbar = document.querySelector('.navbar');
    if (window.scrollY > 50) {
        navbar.style.backgroundColor = '#141414';
        navbar.classList.add('shadow');
    } else {
        navbar.style.background = 'linear-gradient(to bottom, rgba(0,0,0,0.7) 10%, rgba(0,0,0,0))';
        navbar.style.backgroundColor = 'transparent';
        navbar.classList.remove('shadow');
    }
};

// 2. Video Modal Logic
const videoModalElement = document.getElementById('videoModal');
const videoModal = new bootstrap.Modal(videoModalElement);
const videoPlayer = document.getElementById('modalVideoPlayer');
const modalHero = document.getElementById('modalHero');

function openVideoModal(card) {
    // Extraction des données depuis l'élément cliqué
    const data = {
        title: card.getAttribute('data-title'),
        description: card.getAttribute('data-description'),
        year: card.getAttribute('data-year'),
        duration: card.getAttribute('data-duration') + 'm',
        thumbnail: card.getAttribute('data-thumbnail'),
        rating: card.getAttribute('data-rating'),
        genre: card.getAttribute('data-genre'),
        trailer: card.getAttribute('data-trailer') // Path: /videos/trailer.mp4
    };

    // Mise à jour des textes de la modale
    document.getElementById('modalTitle').textContent = data.title;
    document.getElementById('modalDescription').textContent = data.description;
    document.getElementById('modalYear').textContent = data.year;
    document.getElementById('modalDuration').textContent = data.duration;
    document.getElementById('modalRating').textContent = data.rating;
    document.getElementById('modalGenre').textContent = data.genre;

    // Gestion du Trailer vs Image
    if (data.trailer && data.trailer !== 'null' && data.trailer !== '') {
        // Afficher la vidéo MP4
        videoPlayer.src = data.trailer;
        videoPlayer.style.display = 'block';
        videoPlayer.load(); // Charge la nouvelle source
        videoPlayer.play().catch(e => console.log("Autoplay blocked or video error:", e));

        modalHero.style.backgroundImage = 'none';
    } else {
        // Fallback sur l'image si pas de trailer
        videoPlayer.pause();
        videoPlayer.style.display = 'none';
        modalHero.style.backgroundImage = `url('${data.thumbnail}')`;
    }

    videoModal.show();
}

// 3. Cleanup when modal is closed
// Empêche la vidéo de continuer à jouer en arrière-plan
videoModalElement.addEventListener('hidden.bs.modal', function () {
    videoPlayer.pause();
    videoPlayer.src = ""; // Décharge la vidéo de la mémoire
    console.log("Modal closed: Video stopped.");
});