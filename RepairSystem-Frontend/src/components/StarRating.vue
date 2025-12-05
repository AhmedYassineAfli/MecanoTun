<template>
<div class="star-rating">
    <div class="stars">
        <span 
            v-for="star in 5" 
            :key="star"
            :class="['star', { 
                'filled': star <= currentRating,
                'half': star === Math.ceil(rating) && rating % 1 !== 0 && !interactive,
                'interactive': interactive
            }]"
            @click="interactive && selectRating(star)"
            @mouseenter="interactive && hoverRating(star)"
            @mouseleave="interactive && (hoveredRating = 0)"
        >
            <span v-if="star <= (interactive ? (hoveredRating || currentRating) : currentRating)" class="star-filled">★</span>
            <span v-else class="star-empty">☆</span>
        </span>
    </div>
    <span v-if="showCount && ratingCount" class="rating-count">({{ ratingCount }} reviews)</span>
</div>
</template>

<script>
export default {
    name: 'StarRating',
    props: {
        rating: {
            type: Number,
            default: 0
        },
        interactive: {
            type: Boolean,
            default: false
        },
        size: {
            type: String,
            default: 'medium' // 'small', 'medium', 'large'
        },
        showCount: {
            type: Boolean,
            default: false
        },
        ratingCount: {
            type: Number,
            default: 0
        }
    },
    data() {
        return {
            currentRating: this.rating,
            hoveredRating: 0
        }
    },
    watch: {
        rating(newVal) {
            this.currentRating = newVal;
        }
    },
    methods: {
        selectRating(stars) {
            this.currentRating = stars;
            this.$emit('rating-selected', stars);
        },
        hoverRating(stars) {
            this.hoveredRating = stars;
        }
    }
}
</script>

<style scoped>
.star-rating {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
}

.stars {
    display: flex;
    gap: 0.25rem;
}

.star {
    font-size: 1.5rem;
    transition: all 0.2s ease;
    cursor: default;
    position: relative;
}

.star.interactive {
    cursor: pointer;
    font-size: 2.5rem;
}

.star.interactive:hover {
    transform: scale(1.2);
}

.star-filled {
    color: #F3BE35;
    text-shadow: 0 0 10px rgba(243, 190, 53, 0.5);
}

.star-empty {
    color: rgba(255, 255, 255, 0.3);
}

.star.interactive .star-filled {
    animation: starGlow 0.3s ease;
}

@keyframes starGlow {
    0% {
        transform: scale(1);
        filter: brightness(1);
    }
    50% {
        transform: scale(1.2);
        filter: brightness(1.5);
    }
    100% {
        transform: scale(1);
        filter: brightness(1);
    }
}

.rating-count {
    font-size: 0.9rem;
    color: var(--text-secondary);
    margin-left: 0.5rem;
}

/* Size variants */
.star-rating.small .star {
    font-size: 1rem;
}

.star-rating.large .star {
    font-size: 2rem;
}
</style>
