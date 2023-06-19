package at.htlleonding.javafx.movieratings.model;

public class MovieRating {
    private String title;
    private int year;
    private double rating;
    private static final int MIN_YEAR = 1888;
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    public MovieRating(String title, int year, double rating) {
        this.setTitle(title);
        this.setYear(year);
        this.setRating(rating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieRating that = (MovieRating) o;
        if (getYear() != that.getYear()) return false;
        return getTitle().equals(that.getTitle());
    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + getYear();
        return result;
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        if (title != null && !title.isEmpty()) {
            this.title = title;
        } else {
            throw new IllegalArgumentException("Title has to be at least 1 character long!");
        }
    }

    public int getYear() {
        return year;
    }

    private void setYear(int year) {
        if (year > MIN_YEAR) {
            this.year = year;
        } else {
            throw new IllegalArgumentException("The year has to be greater than " + MIN_YEAR + "!");
        }
    }

    public double getRating() {
        return rating;
    }

    private void setRating(double rating) {
        if (rating >= MIN_RATING && rating <= MAX_RATING) {
            this.rating = rating;
        } else {
            throw new IllegalArgumentException("The rating has to be greater or equal " + MIN_RATING + " and smaller or equal " + MAX_RATING + "!");
        }
    }
}
