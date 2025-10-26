// Image handling functions
function showImage(imageId) {
    const modal = document.getElementById('imageModal');
    const modalImg = document.getElementById('fullImage');
    modal.style.display = 'block';
    modalImg.src = `/api/images/${imageId}`;
}

function closeImageModal() {
    const modal = document.getElementById('imageModal');
    modal.style.display = 'none';
}

// Close modal when clicking outside the image
document.addEventListener('click', function(event) {
    const modal = document.getElementById('imageModal');
    const modalImg = document.getElementById('fullImage');
    if (event.target !== modalImg && modal.style.display === 'block') {
        closeImageModal();
    }
});