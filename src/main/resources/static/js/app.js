// Simple script to handle navbar scroll effect
window.onscroll = function () {
    var navbar = document.querySelector('.navbar');
    if (window.scrollY > 50) {
        navbar.style.backgroundColor = '#141414';
    } else {
        navbar.style.background = 'linear-gradient(to bottom, rgba(0,0,0,0.7) 10%, rgba(0,0,0,0))';
        navbar.style.backgroundColor = 'transparent';
    }
};

// Handle Video Modal
const videoModal = new bootstrap.Modal(document.getElementById('videoModal'));

function openVideoModal(card) {
    const title = card.getAttribute('data-title');
    const description = card.getAttribute('data-description');
    const year = card.getAttribute('data-year');
    const duration = card.getAttribute('data-duration') + 'm';
    const thumbnail = card.getAttribute('data-thumbnail');
    // const rating = card.getAttribute('data-rating'); // Not using it for badge yet

    document.getElementById('modalTitle').textContent = title;
    document.getElementById('modalDescription').textContent = description;
    document.getElementById('modalYear').textContent = year;
    document.getElementById('modalDuration').textContent = duration;
    document.getElementById('modalHero').style.backgroundImage = `url('${thumbnail}')`;

    videoModal.show();
}
