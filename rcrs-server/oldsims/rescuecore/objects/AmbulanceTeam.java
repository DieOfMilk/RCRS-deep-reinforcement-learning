/*
 * Last change: $Date: 2004/05/04 03:09:38 $
 * $Revision: 1.4 $
 *
 * Copyright (c) 2004, The Black Sheep, Department of Computer Science, The University of Auckland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of The Black Sheep, The Department of Computer Science or The University of Auckland nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package rescuecore.objects;

import rescuecore.RescueObject;
import rescuecore.RescueConstants;

/**
   Encapsulation of a TYPE_AMBULANCE_TEAM object
   @see RescueConstants#TYPE_AMBULANCE_TEAM
 */
public class AmbulanceTeam extends Humanoid {
    public AmbulanceTeam() {
		super();
    }

	public AmbulanceTeam(int pos, int extra, int dir, int[] history, int stam, int health, int dmg, int bury) {
		super(pos,extra,dir,history,stam,health,dmg,bury);
	}

	public int getType() {
		return RescueConstants.TYPE_AMBULANCE_TEAM;
	}
}
