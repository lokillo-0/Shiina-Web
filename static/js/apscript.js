function confirmation(location) {
    result = confirm('Are you sure you want to delete this?');
    if(result) {
        window.location = location;
    }
}