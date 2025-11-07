package com.runanywhere.runanywhereai.data.offline

/**
 * Offline emergency guides for critical situations when AI is unavailable.
 * Contains pre-written first-aid and emergency procedures.
 */
class OfflineEmergencyGuides {

    private val guides = mapOf(
        // First Aid
        "bleeding" to """
            🩹 SEVERE BLEEDING - IMMEDIATE ACTION:
            
            1. Call 911 immediately if severe
            2. Protect yourself - wear gloves if available
            3. Apply direct pressure with clean cloth
            4. Maintain pressure for 10-15 minutes
            5. If blood soaks through, add more cloth on top
            6. Elevate the injury above heart level if possible
            7. Do NOT remove embedded objects
            8. Keep person warm and calm
            9. Monitor for shock (pale skin, rapid pulse)
            
            ⚠️ GET EMERGENCY HELP IF:
            - Bleeding doesn't stop after 10 minutes
            - Blood is spurting out
            - Wound is deep or long
            - Object is embedded in wound
        """.trimIndent(),

        "cpr" to """
            🫀 CPR - CARDIOPULMONARY RESUSCITATION:
            
            1. CHECK: Is person responsive? Tap and shout
            2. CALL 911 immediately
            3. CHECK BREATHING: Look, listen, feel (10 seconds max)
            
            IF NOT BREATHING:
            4. Place person on firm, flat surface
            5. Kneel beside chest
            6. Hand position: Center of chest between nipples
            7. Place heel of one hand, other hand on top
            8. Interlock fingers, keep arms straight
            
            COMPRESSIONS:
            - Push hard and fast: 2 inches deep
            - Rate: 100-120 per minute (2 per second)
            - Count: 30 compressions
            
            RESCUE BREATHS (if trained):
            - Tilt head back, lift chin
            - Pinch nose shut
            - Give 2 breaths (each 1 second)
            - Watch for chest rise
            
            CONTINUE: 30 compressions, 2 breaths
            DO NOT STOP until help arrives or person breathes
            
            ⚠️ FOR CHILDREN: Use one hand, compress 2 inches
            ⚠️ FOR INFANTS: Use 2 fingers, compress 1.5 inches
        """.trimIndent(),

        "choking" to """
            😮 CHOKING - HEIMLICH MANEUVER:
            
            SIGNS OF CHOKING:
            - Can't speak or cough
            - Hands at throat
            - Turning blue
            
            IF PERSON IS CONSCIOUS:
            1. Ask "Are you choking?"
            2. Stand behind person
            3. Wrap arms around waist
            4. Make fist above navel, below ribs
            5. Grasp fist with other hand
            6. Quick upward thrusts into abdomen
            7. Repeat 5 times
            8. Continue until object comes out
            
            IF PERSON BECOMES UNCONSCIOUS:
            1. Lower to ground
            2. Call 911
            3. Begin CPR (30 compressions, 2 breaths)
            4. Look for object before giving breaths
            
            FOR INFANTS (under 1 year):
            1. Hold face-down on forearm
            2. Support head and neck
            3. Give 5 back blows between shoulder blades
            4. Turn over, 5 chest thrusts (2 fingers)
            5. Alternate until object comes out
            
            ⚠️ CALL 911 IMMEDIATELY if choking continues
        """.trimIndent(),

        "burns" to """
            🔥 BURN TREATMENT:
            
            FIRST: Stop the burning
            - Remove from heat source
            - Remove jewelry/tight clothing
            - Do NOT remove stuck clothing
            
            FOR MINOR BURNS (1st/2nd degree, small area):
            1. Cool with cool (not cold) running water 10-20 min
            2. Remove jewelry before swelling
            3. Apply clean, dry bandage
            4. Give pain reliever if needed
            5. Keep burn elevated
            
            DO NOT:
            ❌ Use ice
            ❌ Apply butter, oil, or ointments
            ❌ Break blisters
            ❌ Remove stuck clothing
            
            SEEK IMMEDIATE MEDICAL HELP IF:
            - Burn is larger than 3 inches
            - Burn is on face, hands, feet, genitals, or joints
            - Burn is 3rd degree (white/charred skin)
            - Caused by chemicals or electricity
            - Person is child or elderly
            - Signs of infection appear
            
            FOR SEVERE BURNS:
            1. Call 911
            2. Cover with clean, dry cloth
            3. Do NOT immerse in water
            4. Treat for shock
            5. Monitor breathing
        """.trimIndent(),

        "earthquake" to """
            🏚️ EARTHQUAKE SAFETY:
            
            DURING EARTHQUAKE:
            1. DROP to hands and knees
            2. COVER head and neck under sturdy desk/table
            3. HOLD ON until shaking stops
            4. If no table: cover head with arms, crouch in corner
            5. Stay away from windows, mirrors, tall furniture
            6. DO NOT run outside during shaking
            
            IF OUTSIDE:
            - Move away from buildings, trees, power lines
            - Get to open area
            - Drop and cover
            
            IF IN CAR:
            - Pull over safely away from bridges/overpasses
            - Stay in vehicle
            - Avoid stopping under trees, power lines, buildings
            
            AFTER EARTHQUAKE:
            1. Check for injuries
            2. Expect aftershocks
            3. Turn off gas if you smell leak
            4. Check for structural damage
            5. Use stairs, not elevators
            6. Stay away from damaged buildings
            7. Listen to emergency radio
            8. Text, don't call (save phone lines)
            
            EVACUATION KIT READY:
            - Water (1 gallon per person per day)
            - Non-perishable food
            - First aid kit
            - Flashlight and batteries
            - Radio
            - Medications
            - Important documents
        """.trimIndent(),

        "flood" to """
            🌊 FLOOD SAFETY:
            
            BEFORE FLOOD:
            1. Move to higher ground immediately
            2. Listen to emergency alerts
            3. Gather emergency supplies
            4. Turn off utilities if instructed
            5. Do NOT walk or drive through water
            
            DURING FLOOD:
            1. Get to highest floor or roof
            2. Call for help
            3. Signal for rescue (flashlight, bright cloth)
            4. Do NOT enter moving water
            5. 6 inches of water can knock you down
            6. 2 feet of water can float a car
            
            WATER SAFETY:
            ❌ NEVER drive through flooded roads
            ❌ NEVER swim in floodwater (contaminated)
            ❌ NEVER touch electrical equipment if wet
            ❌ NEVER drink floodwater
            
            AFTER FLOOD:
            1. Wait for all-clear from authorities
            2. Avoid floodwater (sewage, chemicals)
            3. Document damage (photos)
            4. Clean and disinfect everything
            5. Check for structural damage
            6. Watch for snakes/animals
            7. Boil water until declared safe
            
            IF TRAPPED IN BUILDING:
            - Go to highest floor
            - Do NOT go into attic (may trap you)
            - Signal for help
            - Call 911
        """.trimIndent(),

        "fire" to """
            🔥 FIRE EVACUATION:
            
            IMMEDIATE ACTIONS:
            1. Alert others - shout "FIRE!"
            2. Activate fire alarm
            3. Call 911
            4. Get out immediately
            5. Feel doors before opening (hot = fire behind it)
            6. Crawl low under smoke
            7. Close doors behind you
            8. Use stairs, NEVER elevators
            9. Go to meeting point
            10. Do NOT go back inside
            
            IF CAUGHT IN SMOKE:
            - Drop to hands and knees
            - Crawl to exit
            - Breathe through cloth if possible
            - Keep head 12-24 inches above floor
            
            IF DOOR IS HOT:
            - Do NOT open
            - Seal door cracks with cloth
            - Signal from window
            - Call 911, give location
            - Wait for rescue
            
            IF CLOTHES CATCH FIRE:
            1. STOP - don't run
            2. DROP - to ground
            3. ROLL - back and forth
            4. Cover face with hands
            5. Cool burn with water
            
            AFTER ESCAPING:
            - Stay outside
            - Account for everyone
            - Tell firefighters if someone is missing
            - Do NOT re-enter until cleared
            
            ⚠️ SMOKE KILLS MORE THAN FLAMES
            ⚠️ YOU HAVE 2 MINUTES TO ESCAPE
        """.trimIndent(),

        "heart_attack" to """
            🫀 HEART ATTACK - ACT FAST:
            
            SIGNS & SYMPTOMS:
            - Chest pain/pressure (may spread to arms, neck, jaw)
            - Shortness of breath
            - Cold sweat
            - Nausea
            - Lightheadedness
            - Women may have different symptoms
            
            IMMEDIATE ACTION:
            1. CALL 911 IMMEDIATELY - every second counts
            2. Chew and swallow aspirin (if not allergic)
            3. Sit or lie down
            4. Stay calm
            5. Loosen tight clothing
            6. If person becomes unconscious, start CPR
            
            DO NOT:
            ❌ Wait to see if symptoms go away
            ❌ Drive to hospital yourself
            ❌ Let person convince you it's nothing
            
            WHILE WAITING FOR AMBULANCE:
            - Keep person calm and still
            - Monitor breathing
            - Be ready to perform CPR
            - Gather medications to give to paramedics
            
            ⚠️ TIME = HEART MUSCLE
            ⚠️ FASTER TREATMENT = BETTER RECOVERY
            ⚠️ CALL 911 EVEN IF NOT SURE
        """.trimIndent(),

        "stroke" to """
            🧠 STROKE - FAST RECOGNITION:
            
            F - FACE: Does one side droop? Ask to smile.
            A - ARMS: Can they raise both arms? Does one drift down?
            S - SPEECH: Is speech slurred? Can they repeat simple phrase?
            T - TIME: Call 911 IMMEDIATELY if any signs present
            
            OTHER SIGNS:
            - Sudden confusion
            - Sudden trouble seeing
            - Sudden trouble walking
            - Sudden severe headache
            - Sudden numbness/weakness (face, arm, leg)
            
            IMMEDIATE ACTION:
            1. Call 911 - note time symptoms started
            2. Keep person calm and comfortable
            3. Do NOT give anything to eat or drink
            4. If unconscious, check breathing
            5. Be ready to perform CPR
            
            CRITICAL TIMING:
            - Treatment most effective within 3 hours
            - Brain damage happens fast
            - Every minute counts
            
            DO NOT:
            ❌ Wait to see if symptoms improve
            ❌ Let person sleep it off
            ❌ Give aspirin (may worsen bleeding stroke)
            ❌ Drive to hospital (ambulance has treatment)
            
            ⚠️ STROKE IS A MEDICAL EMERGENCY
            ⚠️ FAST = Face, Arms, Speech, Time
            ⚠️ CALL 911 IMMEDIATELY
        """.trimIndent()
    )

    fun getGuide(query: String): String? {
        val lowerQuery = query.lowercase().trim()

        // Try exact match first
        guides[lowerQuery]?.let { return it }

        // Try keyword matching
        for ((key, guide) in guides) {
            if (lowerQuery.contains(key) || key.contains(lowerQuery)) {
                return guide
            }
        }

        // Common synonyms and variations
        return when {
            lowerQuery.contains("bleed") -> guides["bleeding"]
            lowerQuery.contains("cpr") || lowerQuery.contains("cardiac") -> guides["cpr"]
            lowerQuery.contains("chok") -> guides["choking"]
            lowerQuery.contains("burn") -> guides["burns"]
            lowerQuery.contains("earthquake") || lowerQuery.contains("quake") -> guides["earthquake"]
            lowerQuery.contains("flood") || lowerQuery.contains("water") && lowerQuery.contains("rising") -> guides["flood"]
            lowerQuery.contains("fire") -> guides["fire"]
            lowerQuery.contains("heart") -> guides["heart_attack"]
            lowerQuery.contains("stroke") -> guides["stroke"]
            else -> null
        }
    }

    fun getAllGuideTopics(): List<String> {
        return guides.keys.toList().sorted()
    }

    fun searchGuides(query: String): List<Pair<String, String>> {
        val lowerQuery = query.lowercase()
        return guides.filter { (key, content) ->
            key.contains(lowerQuery) || content.lowercase().contains(lowerQuery)
        }.toList()
    }
}