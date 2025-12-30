#version 150
// Enhanced Thermal Vision Shader
// Based on https://www.shadertoy.com/view/4XtSR4
// Modified for better visibility and stability

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

uniform sampler2D DiffuseSampler;
uniform float Time;
uniform vec2 InSize;

uniform float VignetteEnabled;       // 0.0 = off, 1.0 = on
uniform float VignetteRadius;        // Default: 0.65
uniform float Brightness;            // Overall brightness multiplier
uniform float NoiseAmplification;    // Strength of noise effect

const vec3 lum = vec3(0.2125, 0.4154, 0.0721);

const float minBlur = 0.0;
const float blurIterations = 2.0;  // Optimized: reduced from 4.0 to 2.0
const float blurDistance = 0.03;
const float pixels = 0.2;

const float MAX_BRIGHTNESS_THRESHOLD = 10.0;
const float MIN_BRIGHTNESS_THRESHOLD = 0.25;
const float BASE_BRIGHTNESS = 0.15;

float random(in vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

vec4 blurTex(in sampler2D tex, in vec2 uv, float off, float it) {
    float subpx = 8.0 * it;
    vec4 fullRes = texture(tex, uv + vec2(0, 0)) / max(1.0, subpx + 1.0);

    for (float i = 0.0; i < it; i++) {
        float o = off * i;
        fullRes += texture(tex, uv + vec2(0, o)) / subpx;
        fullRes += texture(tex, uv + vec2(o, o)) / subpx;
        fullRes += texture(tex, uv + vec2(o, 0)) / subpx;
        fullRes += texture(tex, uv + vec2(o, -o)) / subpx;
        fullRes += texture(tex, uv + vec2(0, -o)) / subpx;
        fullRes += texture(tex, uv + vec2(-o, -o)) / subpx;
        fullRes += texture(tex, uv + vec2(-o, 0)) / subpx;
        fullRes += texture(tex, uv + vec2(-o, o)) / subpx;
    }
    return fullRes;
}

float getAverageSceneBrightness(sampler2D tex) {
    // Optimized: reduced from 7x7 (49 samples) to 3x3 (9 samples)
    const int SAMPLE_COUNT_X = 3;
    const int SAMPLE_COUNT_Y = 3;

    vec3 totalBrightness = vec3(0.0);
    float weightSum = 0.0;
    float maxBrightnessSample = 0.0;

    for (int y = 0; y < SAMPLE_COUNT_Y; y++) {
        float yPos = 0.2 + 0.6 * (float(y) / float(SAMPLE_COUNT_Y - 1));

        for (int x = 0; x < SAMPLE_COUNT_X; x++) {
            float xPos = 0.2 + 0.6 * (float(x) / float(SAMPLE_COUNT_X - 1));

            float weight = 1.0 - (distance(vec2(xPos, yPos), vec2(0.5, 0.5)) * 1.2);
            weight = max(0.2, weight * weight);
            vec3 sampleColor = textureLod(tex, vec2(xPos, yPos), 5.0).rgb;

            float sampleBrightness = dot(sampleColor, lum);
            maxBrightnessSample = max(maxBrightnessSample, sampleBrightness);

            if (sampleBrightness > 1.0) {
                sampleBrightness = 1.0 + log(sampleBrightness);
            }

            totalBrightness += sampleColor * weight;
            weightSum += weight;
        }
    }

    vec3 averageBrightness = totalBrightness / max(0.001, weightSum);
    float brightness = dot(averageBrightness, lum);

    if (maxBrightnessSample > MAX_BRIGHTNESS_THRESHOLD) {
        float dampFactor = 1.0 / (1.0 + log(maxBrightnessSample / MAX_BRIGHTNESS_THRESHOLD));
        brightness *= dampFactor;
    }

    if (maxBrightnessSample < 0.1) {
        brightness = mix(brightness + BASE_BRIGHTNESS, brightness, maxBrightnessSample * 10.0);
    }

    float remappedBrightness = 0.5 + 0.45 * tanh((brightness - 0.5) * 2.0);
    remappedBrightness = max(MIN_BRIGHTNESS_THRESHOLD, remappedBrightness);

    return remappedBrightness;
}

vec4 thermalVision(sampler2D tex, vec2 uv, vec2 fragCoord) {
    float resScale = 1440.0 / InSize.y;
    float noisePixels = (pixels < 1.0 ? pixels : pixels * 0.2) * resScale;

    vec4 fullRes = texture(tex, uv);

    float vignette = (VignetteEnabled > 0.5) ?
    pow(1.0 - dot(uv - 0.5, uv - 0.5), 2.2) * 1.2 : 1.0;

    float blurVignette = clamp(1.0 - pow(vignette, 1.4), 0.0, 1.0) + minBlur;
    vignette = pow(vignette, 3.0);

    vec4 fullResBlur = blurTex(tex, uv, blurDistance / blurIterations * blurVignette, blurIterations);
    fullRes = fullResBlur;

    vec2 pixelCoord = floor(fragCoord * pixels) / pixels;
    vec4 pixelRes = textureLod(tex, pixelCoord / InSize, 0.0);
    vec4 fragResult = mix(pixelRes, fullRes, 0.5);

    // this is some redudant shit tbh
    float pixelBrightness = dot(pixelRes.rgb, lum);
    if (pixelBrightness < 0.1) {
        pixelRes.rgb += vec3(BASE_BRIGHTNESS * (1.0 - pixelBrightness * 5.0));
        fragResult = mix(pixelRes, fullRes, 0.5);
    }

    float sceneBrightness = getAverageSceneBrightness(tex);

    if (uv.y > 0.98 && uv.x > 0.49 && uv.x < 0.51)
    return vec4(sceneBrightness, sceneBrightness, sceneBrightness, 1.0);

    float mul = 1.2;
    float brightnessFactor = smoothstep(0.0, 0.5, sceneBrightness);

    float adjustedSceneBrightness = max(0.2, sceneBrightness);
    mul = mix(pow(adjustedSceneBrightness, -0.8), mul, brightnessFactor);

    mul = max(0.6, mul);

    fragResult *= mul * Brightness * 1.2;

    float grey = dot(fragResult.rgb, lum);

    grey = pow(grey, 0.65);

    grey = smoothstep(0.08, 0.9, grey);  // Changed from 0.1-0.85 to 0.08-0.9
    grey = max(0.05, grey);

    float pulseFactor = 0.05 * sin(Time * 3.0);

    float baseTone = mix(0.08, 0.95, grey);
    baseTone = pow(baseTone, 0.9);
    baseTone *= 1.0 + pulseFactor * grey;

    vec3 grayscaleMask = vec3(baseTone);

    vec4 center = fragResult;

    // Optimized: reduced edge detection samples from 4 to 2
    vec4 neighbors[2];
    neighbors[0] = texture(tex, uv + vec2(0.01, 0.0));
    neighbors[1] = texture(tex, uv + vec2(0.0, 0.01));

    float edgeContrast = 0.0;
    for (int i = 0; i < 2; i++) {
        float neighborGrey = dot(neighbors[i].rgb, lum);
        edgeContrast += abs(grey - neighborGrey);
    }

    if (edgeContrast > 0.12) {
        float edgeBoost = clamp(edgeContrast * 2.0, 0.0, 0.35);
        grayscaleMask = mix(grayscaleMask, vec3(1.0), edgeBoost);
    }

    float noiseTime = mod(Time * 0.15, 10.0);
    float noise = random(uv / resScale + noiseTime);

    vec2 noiseUv = floor(fragCoord * noisePixels) / InSize / noisePixels;
    float highNoise = pow(random(noiseUv + noiseTime), 1000.0) * 1.0;
    highNoise += highNoise * vignette * 10.0;

    float noiseDarknessFactor = smoothstep(0.0, 0.3, grey);
    float thermalNoise = (noise * 0.3 + highNoise * 1.0) * noiseDarknessFactor;

    vec3 finalColor = (vec3(grey) + vec3(thermalNoise)) * grayscaleMask * vignette;
    float finalBrightness = dot(finalColor, lum);

    if (finalBrightness < 0.1) {
        finalColor = mix(vec3(0.1) * grayscaleMask, finalColor, finalBrightness * 10.0);
    }

    return vec4(finalColor, 1.0);
}

void main() {
    fragColor = thermalVision(DiffuseSampler, texCoord, texCoord * InSize);
}
